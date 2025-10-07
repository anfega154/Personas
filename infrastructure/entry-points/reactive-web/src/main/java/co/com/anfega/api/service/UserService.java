package co.com.anfega.api.service;

import co.com.anfega.api.dto.RequestByIdsDTO;
import co.com.anfega.api.helper.client.ApiResponse;
import co.com.anfega.api.helper.client.WebClientHelper;
import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.user.gateways.UserInputPort;
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


    public Mono<Void> enrollUserInBootcamp(Long userId, List<Long> bootcampIds) {
        return getBootcampsByIds(bootcampIds)
                .flatMap(bootcamps -> userInputPort.enrollUserInBootcamp(userId, bootcamps))
                .doOnSuccess(unused -> log.info("Usuario {} inscrito en los bootcamps {}", userId, bootcampIds))
                .doOnError(e -> log.error("Error al inscribir usuario {} en los bootcamps {}: {}", userId, bootcampIds, e.getMessage(), e));
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
                .doOnSuccess(response -> log.info("Bootcaps obtenidos: {}", response))
                .map(ApiResponse::getContent)
                .onErrorResume(e -> {
                    log.error("Error al obtener Bootcaps: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("No se pudieron obtener los Bootcaps, valide la lista de Bootcaps", e));
                });
    }
}
