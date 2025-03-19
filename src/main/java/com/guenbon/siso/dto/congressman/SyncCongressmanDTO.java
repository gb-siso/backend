package com.guenbon.siso.dto.congressman;

import com.guenbon.siso.entity.congressman.Congressman;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class SyncCongressmanDTO {
    private Congressman congressman;
    private List<Integer> assemblySessions;

    public static SyncCongressmanDTO of(Congressman congressman, List<Integer> assemblySessions) {
        return SyncCongressmanDTO.builder().congressman(congressman).assemblySessions(assemblySessions).build();
    }
}
