package co.com.anfega.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Table("user_bootcamp")
public class UserBootcampEntity {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("bootcamp_id")
    private Long bootcampId;

    @Column("registered_at")
    private LocalDate registeredAt;
}
