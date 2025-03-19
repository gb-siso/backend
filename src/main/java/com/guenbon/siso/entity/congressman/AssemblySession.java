package com.guenbon.siso.entity.congressman;

import com.guenbon.siso.entity.common.DateEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@ToString
public class AssemblySession extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "congressman_id", nullable = false)
    private Congressman congressman;

    @Column(nullable = false)
    private Integer session;

    public static AssemblySession of(Congressman congressman, Integer session) {
        return AssemblySession.builder().congressman(congressman).session(session).build();
    }
}
