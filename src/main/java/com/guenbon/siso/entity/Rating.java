package com.guenbon.siso.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
}
