package co.com.anfega.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("Persona")
public class UserEntity {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("email")
    private String email;
}
