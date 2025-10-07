package co.com.anfega.model.user.gateways;

import co.com.anfega.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findById(Long id);
}
