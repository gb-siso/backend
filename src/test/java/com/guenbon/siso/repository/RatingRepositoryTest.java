package com.guenbon.siso.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class RatingRepositoryTest {

    @Autowired
    RatingRepository repository;

    @Test
    void ratingRepository_null_아님() {
        assertThat(repository).isNotNull();
    }

}