package com.guenbon.siso.dto.congressman.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@Builder
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

    public static CongressmanBatchResultDTO of(LocalDateTime time, List<CongressmanDTO> batchInsertResult, int batchUpdateCount, int batchRemoveCount) {
        return CongressmanBatchResultDTO.builder()
                .time(time)
                .batchInsertResult(batchInsertResult)
                .batchInsertCount(batchInsertResult.size())
                .batchUpdateCount(batchUpdateCount)
                .batchRemoveCount(batchRemoveCount).build();
    }
}
