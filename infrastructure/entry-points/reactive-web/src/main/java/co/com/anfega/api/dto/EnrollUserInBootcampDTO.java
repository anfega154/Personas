package co.com.anfega.api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class EnrollUserInBootcampDTO {
    private Long userId;
    @Size(min = 1, max = 5, message = "La cantidad de bootcamps debe estar entre 1 y 5")
    private List<Long> bootcampIds;
}
