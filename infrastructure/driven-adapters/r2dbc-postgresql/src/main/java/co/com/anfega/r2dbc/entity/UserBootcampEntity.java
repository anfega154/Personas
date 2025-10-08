package co.com.anfega.r2dbc.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("user_bootcamp")
public class UserBootcampEntity {

    @Column("user_id")
    private Long userId;

    @Column("bootcamp_id")
    private Long bootcampId;
}
