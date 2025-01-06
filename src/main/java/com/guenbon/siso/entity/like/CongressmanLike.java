package com.guenbon.siso.entity.like;

import com.guenbon.siso.entity.Congressman;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CongressmanLike extends Like {
    @ManyToOne
    @JoinColumn(name = "congressman_id")
    private Congressman congressman;
}
