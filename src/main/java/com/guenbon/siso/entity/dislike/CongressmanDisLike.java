package com.guenbon.siso.entity.dislike;

import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.like.Like;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CongressmanDisLike extends DisLike {
    @ManyToOne
    @JoinColumn(name = "congressman_id")
    private Congressman congressman;
}
