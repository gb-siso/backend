package com.guenbon.siso.entity.like;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.common.DateEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`like`",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"rating_id", "member_id"})
        })
public class Like extends DateEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    protected Member member;
}
