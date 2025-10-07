package co.com.anfega.api;

import co.com.anfega.api.dto.EnrollUserInBootcampDTO;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import jakarta.validation.Validator;

@Component
@RequiredArgsConstructor
public class Handler extends BaseHandler {

    private final UserService userService;
    private final Validator validator;

    public Mono<ServerResponse> listenGetUserById(ServerRequest request) {
        return bodyToMonoValidated(validator, request, EnrollUserInBootcampDTO.class)
                .flatMap(dto -> userService.enrollUserInBootcamp(dto.getUserId(), dto.getBootcampIds()))
                .then(Mono.defer(() -> ok("Bootcamps inscritos con exito")));
    }

}
