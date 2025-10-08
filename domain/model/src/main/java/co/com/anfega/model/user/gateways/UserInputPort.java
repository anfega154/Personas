package co.com.anfega.model.user.gateways;

import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserInputPort {
    Mono<Void> enrollUserInBootcamp(Long userId, List<Bootcamp> bootcamps);
    Mono<User> findById(Long userId);
}
