package co.com.anfega.r2dbc;

import co.com.anfega.r2dbc.entity.UserBootcampEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserBootcampReactiveRepository extends ReactiveCrudRepository<UserBootcampEntity, Long>, ReactiveQueryByExampleExecutor<UserBootcampEntity> {
  Mono<Long> countByUserId(Long userId);
}
