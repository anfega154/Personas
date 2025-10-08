package co.com.anfega.sqs.sender.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbilityDTO {
    private Long id;
    private String name;
    private String description;
}
