package com.guenbon.siso.controller.docs;

import com.guenbon.siso.dto.congressman.CongressmanListDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "국회의원 API")
public interface CongressmanControllerDocs {

    @Operation(summary = "회원가입", description = "카카오 식별 id, imageUrl, nickname 포함 회원가입 요청")
    @Parameters(value = {
            @Parameter(name = "size", description = "한번에 요청할 데이터 개수 (default : 20)"),
            @Parameter(name = "cursor", description = "무한스크롤 구현 위한 값 , 응답에 있는 값 그대로 사용하면 됨 (default : Long 최댓값 (최초 요청시))"),
            @Parameter(name = "sort", description = "정렬 방식 (값 : topicality(기본값), rating, rating일경우 DESC, ASC 선택", example = "sort=rating,DESC"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "국회의원 목록 (홈페이지)", content = @Content(schema = @Schema(implementation = CongressmanListDTO.class)))
    })
    ResponseEntity<CongressmanListDTO> congressmanList(@RequestParam String party, Pageable pageable, @RequestParam Long size, @RequestParam Long cursor);
}
