package com.guenbon.siso.dto.congressman.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class CongressmanBatchResultDTO {

    private LocalDateTime time;
    private List<CongressmanDTO> batchInsertResult;
    private int batchRemoveResultCount;
    private int batchRemoveUpdateCount;

    @AllArgsConstructor
    @Getter
    public static class CongressmanDTO {
        private String id;
        private String code;
        private String name;
    }

    public static CongressmanBatchResultDTO of(LocalDateTime time, List<CongressmanDTO> batchInsertResult, int batchRemoveUpdateCount, int batchRemoveResultCount) {
        return new CongressmanBatchResultDTO(time, batchInsertResult, batchRemoveUpdateCount, batchRemoveResultCount);
    }
}
