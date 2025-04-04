package com.guenbon.siso.dto.congressman.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class CongressmanBatchResultDTO {

    private LocalDateTime time;
    private List<CongressmanDTO> batchInsertResult;
    private int batchInsertCount;
    private int batchUpdateCount;
    private int batchRemoveCount;

    @AllArgsConstructor
    @Getter
    public static class CongressmanDTO {
        private String id;
        private String code;
        private String name;
    }

    public static CongressmanBatchResultDTO of(List<CongressmanDTO> batchInsertResult, int batchUpdateCount, int batchRemoveCount) {
        return CongressmanBatchResultDTO.builder()
                .time(LocalDateTime.now())
                .batchInsertResult(batchInsertResult)
                .batchInsertCount(batchInsertResult.size())
                .batchUpdateCount(batchUpdateCount)
                .batchRemoveCount(batchRemoveCount).build();
    }
}
