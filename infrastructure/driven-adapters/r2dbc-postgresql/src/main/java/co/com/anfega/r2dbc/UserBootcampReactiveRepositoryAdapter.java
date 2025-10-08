package co.com.anfega.r2dbc;

import co.com.anfega.model.userbootcamp.UserBootcamp;
import co.com.anfega.model.userbootcamp.gateways.UserBootcampRepository;
import co.com.anfega.r2dbc.entity.UserBootcampEntity;
import co.com.anfega.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public class UserBootcampReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        UserBootcamp,
        UserBootcampEntity,
        Void,
        UserBootcampReactiveRepository
        > implements UserBootcampRepository {
    public UserBootcampReactiveRepositoryAdapter(UserBootcampReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, UserBootcamp.class/* change for domain model */));
    }

    @Override
    public Mono<UserBootcamp> save(UserBootcamp userBootcamp) {
        return Mono.just(userBootcamp)
                .map(ub -> {
                    UserBootcampEntity entity = new UserBootcampEntity();
                    entity.setUserId(ub.getUserId());
                    entity.setBootcampId(ub.getBootcampId());
                    return entity;
                })
                .flatMap(repository::save)
                .map(entity -> mapper.map(entity, UserBootcamp.class))
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error creando el registro: " + e.getMessage())));
    }

    @Override
    public Mono<Long> countByUserId(Long userId) {
        return repository.countByUserId(userId)
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error contando registros: " + e.getMessage())));
    }
}
