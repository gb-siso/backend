package com.guenbon.siso.support.fixture;

import com.guenbon.siso.entity.Congressman;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CongressmanFixture {

    private Long id;

    private String name;

    public static CongressmanFixture builder() {
        return new CongressmanFixture();
    }

    public CongressmanFixture setId(Long id) {
        this.id = id;
        return this;
    }

    public CongressmanFixture setName(String name) {
        this.name = name;
        return this;
    }

    public Congressman build() {
        return Congressman.builder()
                .name(name)
                .build();
    }
}
