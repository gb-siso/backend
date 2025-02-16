package com.guenbon.siso.entity.dislike;

import com.guenbon.siso.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "dislike",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"rating_id", "member_id"})
        })
public class DisLike {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;
}
