package co.com.anfega.api.service;

import co.com.anfega.api.helper.Util;
import co.com.anfega.model.technology.Technology;
import co.com.anfega.model.user.User;
import co.com.anfega.sqs.sender.events.AbilityDTO;
import co.com.anfega.sqs.sender.events.BootcampCreatedEvent;
import co.com.anfega.api.dto.RequestByIdsDTO;
import co.com.anfega.sqs.sender.events.TechnologyDTO;
import co.com.anfega.api.helper.client.ApiResponse;
import co.com.anfega.api.helper.client.WebClientHelper;
import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.user.gateways.UserInputPort;
import co.com.anfega.sqs.sender.config.BootcampEventSerializationException;
import co.com.anfega.sqs.sender.events.BootcampEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserInputPort userInputPort;
    private final WebClientHelper webClientHelper;
    private final BootcampEventPublisher publisher;

    public Mono<Void> enrollUserInBootcamp(Long userId, List<Long> bootcampIds) {
        return userInputPort.findById(userId)
                .flatMap(user -> getBootcampsByIds(bootcampIds)
                        .flatMap(bootcamps ->
                                userInputPort.enrollUserInBootcamp(userId, bootcamps)
                                        .then(publishBootcampEventsSafely(bootcamps, user))
                                        .onErrorResume(e -> {
                                            log.error("Error al inscribir usuario {} en bootcamps {}: {}", userId, bootcampIds, e.getMessage(), e);
                                            return Mono.error(e);
                                        })
                        )
                )
                .doOnSuccess(unused -> log.info("Usuario {} inscrito en los bootcamps {}", userId, bootcampIds))
                .onErrorResume(e -> {
                    log.error("Fallo general en enrollUserInBootcamp para usuario {}: {}", userId, e.getMessage(), e);
                    return Mono.error(new RuntimeException("Error al inscribir usuario en bootcamps", e));
                });
    }

    private Mono<Void> publishBootcampEventsSafely(List<Bootcamp> bootcamps, User user) {
        return Mono.when(
                bootcamps.stream()
                        .map(bootcamp -> createBootcampEvent(bootcamp, user))
                        .map(this::publishEventSafely)
                        .toList()
        ).onErrorResume(e -> {
            log.error("Error al publicar uno o más eventos BootcampCreatedEvent: {}", e.getMessage(), e);
            return Mono.empty(); // no interrumpe el flujo
        });
    }

    private BootcampCreatedEvent createBootcampEvent(Bootcamp bootcamp, User user) {
        return BootcampCreatedEvent.builder()
                .id(bootcamp.getId())
                .name(bootcamp.getName())
                .description(bootcamp.getDescription())
                .launchDate(String.valueOf(bootcamp.getReleaseDate()))
                .duration(bootcamp.getDuration() + " Días")
                .abilities(bootcamp.getAbilities().stream()
                        .map(a -> AbilityDTO.builder()
                                .id(a.getId())
                                .name(a.getName())
                                .description(a.getDescription())
                                .build())
                        .toList())
                .technologies(bootcamp.getAbilities().stream()
                        .flatMap(a -> a.getTechnologies().stream())
                        .filter(Util.distinctByKey(Technology::getId))
                        .map(t -> TechnologyDTO.builder()
                                .id(t.getId())
                                .name(t.getName())
                                .description(t.getDescription())
                                .build())
                        .toList())
                .capabilitiesCount(bootcamp.getAbilities().size())
                .technologiesCount(bootcamp.getAbilities().stream()
                        .mapToInt(a -> a.getTechnologies() != null ? a.getTechnologies().size() : 0)
                        .sum())
                .participantsCount(1)
                .userId(user.getId())
                .userName(user.getName())
                .email(user.getEmail())
                .build();
    }

    private Mono<Void> publishEventSafely(BootcampCreatedEvent event) {
        try {
            return publisher.publishBootcampCreatedEvent(event)
                    .onErrorResume(e -> {
                        log.error("Error al publicar BootcampCreatedEvent (id={}): {}", event.getId(), e.getMessage(), e);
                        return Mono.empty(); // el enrolamiento no falla si el evento falla
                    });
        } catch (BootcampEventSerializationException e) {
            log.error("Error de serialización al crear evento BootcampCreatedEvent (id={}): {}", event.getId(), e.getMessage());
            return Mono.empty();
        }
    }

    private Mono<List<Bootcamp>> getBootcampsByIds(List<Long> ids) {
        RequestByIdsDTO listIds = new RequestByIdsDTO();
        listIds.setIds(ids);

        return webClientHelper.post(
                        "http://localhost:8090/api/v1/bootcamp/validate",
                        null,
                        listIds,
                        new ParameterizedTypeReference<ApiResponse<List<Bootcamp>>>() {
                        }
                )
                .doOnSuccess(response -> log.info("Bootcamps obtenidos: {}", response))
                .map(ApiResponse::getContent)
                .onErrorResume(e -> {
                    log.error("Error al obtener Bootcamps: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("No se pudieron obtener los Bootcamps, valide la lista de Bootcamps", e));
                });
    }
}
