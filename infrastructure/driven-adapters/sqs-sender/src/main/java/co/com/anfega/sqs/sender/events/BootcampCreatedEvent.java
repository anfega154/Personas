package co.com.anfega.sqs.sender.events;

import co.com.anfega.sqs.sender.config.BootcampEventSerializationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BootcampCreatedEvent {
    private Long id;
    private String name;
    private String description;
    private String launchDate;
    private String duration;
    private List<AbilityDTO> abilities;
    private List<TechnologyDTO> technologies;
    private int capabilitiesCount;
    private int technologiesCount;
    private int participantsCount;
    private Long userId;
    private String userName;
    private String email;

    public String toJson() throws BootcampEventSerializationException {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new BootcampEventSerializationException("Error serializando BootcampCreatedEvent", e);
        }
    }
}

