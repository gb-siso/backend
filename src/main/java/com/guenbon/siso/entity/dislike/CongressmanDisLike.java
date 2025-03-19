package com.guenbon.siso.entity.dislike;

import com.guenbon.siso.entity.congressman.Congressman;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CongressmanDisLike extends DisLike {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "congressman_id")
    private Congressman congressman;
}
