package com.guenbon.siso.support.fixture.congressman;

import com.guenbon.siso.dto.congressman.SyncCongressmanDTO;
import com.guenbon.siso.entity.congressman.Congressman;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class SyncCongressmanDTOFixture {
    private Congressman congressman;
    private Set<Integer> assemblySessions = Set.of(22);

    public SyncCongressmanDTOFixture setCongressman(Congressman congressman) {
        this.congressman = congressman;
        return this;
    }

    public SyncCongressmanDTOFixture setAssemblySessions(Set<Integer> assemblySessions) {
        this.assemblySessions = assemblySessions;
        return this;
    }

    public static SyncCongressmanDTOFixture builder() {
        return new SyncCongressmanDTOFixture();
    }

    public SyncCongressmanDTO build() {
        return SyncCongressmanDTO.builder()
                .congressman(congressman)
                .assemblySessions(assemblySessions)
                .build();
    }
}
