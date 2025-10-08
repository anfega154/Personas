package co.com.anfega.usecase.user;

import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.user.User;
import co.com.anfega.model.user.gateways.UserInputPort;
import co.com.anfega.model.user.gateways.UserRepository;
import co.com.anfega.model.userbootcamp.UserBootcamp;
import co.com.anfega.model.userbootcamp.gateways.UserBootcampRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public class UserUseCase implements UserInputPort {

    private final UserRepository userRepository;
    private final UserBootcampRepository userBootcampRepository;

    public UserUseCase(UserRepository userRepository, UserBootcampRepository userBootcampRepository) {
        this.userRepository = userRepository;
        this.userBootcampRepository = userBootcampRepository;
    }

    @Override
    public Mono<Void> enrollUserInBootcamp(Long userId, List<Bootcamp> bootcamps) {
        return userBootcampRepository.countByUserId(userId)
                .flatMap(currentCount -> {
                    int newTotal = Math.toIntExact(currentCount + bootcamps.size());
                    if (currentCount >= 5) {
                        return Mono.error(new IllegalStateException("El usuario ya está inscrito en el número máximo de bootcamps"));
                    }
                    if (newTotal > 5) {
                        return Mono.error(new IllegalStateException("El usuario excede el número máximo de bootcamps al inscribirse en estos bootcamps"));
                    }

                    return userRepository.findById(userId)
                            .switchIfEmpty(Mono.error(new IllegalStateException("El usuario no existe")))
                            .flatMap(user -> validateNoDateOverlap(bootcamps))
                            .thenMany(Flux.fromIterable(bootcamps)
                                    .flatMap(bootcamp -> {
                                        UserBootcamp userBootcamp = new UserBootcamp();
                                        userBootcamp.setUserId(userId);
                                        userBootcamp.setBootcampId(bootcamp.getId());
                                        return userBootcampRepository.save(userBootcamp);
                                    }))
                            .then();
                });
    }

    @Override
    public Mono<User> findById(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalStateException("El usuario no existe")));
    }


    private Mono<Void> validateNoDateOverlap(List<Bootcamp> bootcamps) {
       boolean hasOverlap = java.util.stream.IntStream.range(0, bootcamps.size())
           .anyMatch(i -> java.util.stream.IntStream.range(i + 1, bootcamps.size())
               .anyMatch(j -> {
                   Bootcamp b1 = bootcamps.get(i);
                   Bootcamp b2 = bootcamps.get(j);
                   LocalDate start1 = b1.getReleaseDate();
                   LocalDate end1 = start1.plusDays(b1.getDuration());
                   LocalDate start2 = b2.getReleaseDate();
                   LocalDate end2 = start2.plusDays(b2.getDuration());
                   return !(end1.isBefore(start2) || end2.isBefore(start1));
               })
           );

       if (hasOverlap) {
           return Mono.error(new IllegalStateException("Hay bootcamps con fechas solapadas"));
       }
       return Mono.empty();
   }
}
