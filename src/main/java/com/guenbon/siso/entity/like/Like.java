package com.guenbon.siso.entity.like;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.common.DateEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Check;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`like`",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"rating_id", "member_id"})
        })
@Check(constraints = "(dtype = 'RatingLike' AND rating_id IS NOT NULL AND congressman_id IS NULL) OR (dtype = 'CongressmanLike' AND congressman_id IS NOT NULL AND rating_id IS NULL)")
public class Like extends DateEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id")
    protected Member member;
}
