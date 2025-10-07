package co.com.anfega.model.userbootcamp.gateways;

import co.com.anfega.model.userbootcamp.UserBootcamp;
import reactor.core.publisher.Mono;

public interface UserBootcampRepository {
    Mono<UserBootcamp> save(UserBootcamp userBootcamp);
    Mono<Long> countByUserId(Long userId);
}
