package com.guenbon.siso.entity;


import com.guenbon.siso.entity.common.DateEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Congressman extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String party;

    @Column(nullable = false)
    private Integer timesElected;

    @Override
    public String toString() {
        return "Congressman{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", party='" + party + '\'' +
                ", timesElected=" + timesElected +
                '}';
    }
}
