package co.com.anfega.usecase.user;

import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.user.User;
import co.com.anfega.model.user.gateways.UserRepository;
import co.com.anfega.model.userbootcamp.UserBootcamp;
import co.com.anfega.model.userbootcamp.gateways.UserBootcampRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    private UserRepository userRepository;
    private UserBootcampRepository userBootcampRepository;
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userBootcampRepository = Mockito.mock(UserBootcampRepository.class);
        userUseCase = new UserUseCase(userRepository, userBootcampRepository);
    }

    private Bootcamp bootcamp(Long id, LocalDate start, int duration) {
        Bootcamp b = new Bootcamp();
        b.setId(id);
        b.setName("Bootcamp " + id);
        b.setReleaseDate(start);
        b.setDuration(duration);
        return b;
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        when(userBootcampRepository.countByUserId(1L)).thenReturn(Mono.just(0L));
        when(userRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.enrollUserInBootcamp(1L, List.of(bootcamp(1L, LocalDate.now(), 10))))
                .expectErrorMatches(e -> e instanceof IllegalStateException &&
                        e.getMessage().equals("El usuario no existe"))
                .verify();
    }

    @Test
    void shouldFailWhenUserHasMaxBootcamps() {
        when(userBootcampRepository.countByUserId(1L)).thenReturn(Mono.just(5L));

        StepVerifier.create(userUseCase.enrollUserInBootcamp(1L, List.of(bootcamp(1L, LocalDate.now(), 10))))
                .expectErrorMatches(e -> e instanceof IllegalStateException &&
                        e.getMessage().equals("El usuario ya está inscrito en el número máximo de bootcamps"))
                .verify();
    }

    @Test
    void shouldFailWhenUserExceedsMaxBootcamps() {
        when(userBootcampRepository.countByUserId(1L)).thenReturn(Mono.just(4L));

        StepVerifier.create(userUseCase.enrollUserInBootcamp(1L, List.of(
                        bootcamp(1L, LocalDate.now(), 10),
                        bootcamp(2L, LocalDate.now().plusDays(15), 10)
                )))
                .expectErrorMatches(e -> e instanceof IllegalStateException &&
                        e.getMessage().equals("El usuario excede el número máximo de bootcamps al inscribirse en estos bootcamps"))
                .verify();
    }

    @Test
    void shouldFailWhenBootcampsOverlap() {
        when(userBootcampRepository.countByUserId(1L)).thenReturn(Mono.just(0L));
        when(userRepository.findById(1L)).thenReturn(Mono.just(new User())); // usuario existente
        LocalDate start = LocalDate.now();
        Bootcamp b1 = bootcamp(1L, start, 10);         // del día 0 al 10
        Bootcamp b2 = bootcamp(2L, start.plusDays(5), 10); // del día 5 al 15

        StepVerifier.create(userUseCase.enrollUserInBootcamp(1L, List.of(b1, b2)))
                .expectErrorMatches(e -> e instanceof IllegalStateException &&
                        e.getMessage().equals("Hay bootcamps con fechas solapadas"))
                .verify();
    }

    // ✅ Caso 5: Registro exitoso sin solapamiento
    @Test
    void shouldEnrollUserSuccessfullyWhenNoOverlap() {
        when(userBootcampRepository.countByUserId(1L)).thenReturn(Mono.just(2L));
        when(userRepository.findById(1L)).thenReturn(Mono.just(new User()));
        when(userBootcampRepository.save(any(UserBootcamp.class))).thenReturn(Mono.just(new UserBootcamp()));

        LocalDate now = LocalDate.now();
        Bootcamp b1 = bootcamp(1L, now, 5);           // 0 al 5
        Bootcamp b2 = bootcamp(2L, now.plusDays(6), 5); // 6 al 11

        StepVerifier.create(userUseCase.enrollUserInBootcamp(1L, List.of(b1, b2)))
                .verifyComplete();

        ArgumentCaptor<UserBootcamp> captor = ArgumentCaptor.forClass(UserBootcamp.class);
        verify(userBootcampRepository, times(2)).save(captor.capture());
        assertEquals(1L, captor.getAllValues().get(0).getUserId());
    }

    @Test
    void validateNoDateOverlapShouldPassWhenNoOverlap() {
        List<Bootcamp> list = List.of(
                bootcamp(1L, LocalDate.now(), 5),
                bootcamp(2L, LocalDate.now().plusDays(6), 5)
        );

        StepVerifier.create(invokeValidateNoDateOverlap(list)).verifyComplete();
    }

    @Test
    void validateNoDateOverlapShouldFailWhenOverlap() {
        List<Bootcamp> list = List.of(
                bootcamp(1L, LocalDate.now(), 10),
                bootcamp(2L, LocalDate.now().plusDays(5), 10)
        );

        StepVerifier.create(invokeValidateNoDateOverlap(list))
                .expectErrorMatches(e -> e instanceof IllegalStateException &&
                        e.getMessage().equals("Hay bootcamps con fechas solapadas"))
                .verify();
    }

    private Mono<Void> invokeValidateNoDateOverlap(List<Bootcamp> bootcamps) {
        try {
            var method = UserUseCase.class.getDeclaredMethod("validateNoDateOverlap", List.class);
            method.setAccessible(true);
            return (Mono<Void>) method.invoke(userUseCase, bootcamps);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
