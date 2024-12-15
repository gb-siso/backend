package com.guenbon.siso.controller.docs;

import com.guenbon.siso.dto.congressman.response.CongressmanDetailDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.support.annotation.LoginId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "국회의원 API")
public interface CongressmanControllerDocs {

    @Operation(summary = "국회의원 리스트 (홈페이지)", description = "정렬, 필터 쿼리파라미터 포함 요청")
    @Parameters(value = {
            @Parameter(name = "size", description = "한번에 요청할 데이터 개수 (default : 20)"),
            @Parameter(name = "cursor", description = "무한스크롤 구현 위한 값 , 응답에 있는 값 그대로 사용하면 됨 (default : Long 최댓값 (최초 요청시))"),
            @Parameter(name = "sort", description = "정렬 방식 (값 : topicality(기본값), rating, rating일경우 DESC, ASC 선택", example = "sort=rating,DESC"),
            @Parameter(name = "party", description = "당으로 필터링하기 위한 당 , default : 값 X"),
            @Parameter(name = "search", description = "검색어 (당 또는 이름)")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "국회의원 목록 (홈페이지)", content = @Content(schema = @Schema(implementation = CongressmanListDTO.class)))
    })
    ResponseEntity<CongressmanListDTO> list(@RequestParam String party, Pageable pageable, @RequestParam Long size, @RequestParam Long cursor, @RequestParam String search);

    @Operation(summary = "국회의원 상세보기", description = "")
    @Parameters(value = {
            @Parameter(name = "size", description = "한번에 요청할 데이터 개수 (default : 20)"),
            @Parameter(name = "cursor", description = "무한스크롤 구현 위한 값 , 응답에 있는 값 그대로 사용하면 됨 (default : Long 최댓값 (최초 요청시))"),
            @Parameter(name = "sort", description = "정렬 방식 (값 : createdAt(기본값), likes, dislkes, topicality", example = "sort=likes"),
            @Parameter(name = "sort", description = "정렬 방식 (값 : createdAt(기본값), likes, dislkes, topicality", example = "sort=likes"),
            @Parameter(name = "id", description = "조회 대상 국회의원 id"),
    })
    @ApiResponses(value = {
            @ApiResponse(headers = {
                    @Header(
                            name = "Authorization",
                            description = "accessToken",
                            schema = @Schema(type = "string", example = "Bearer abc123")
                    )
            },
                    responseCode = "200", description = "국회의원 목록 (홈페이지)", content = @Content(schema = @Schema(implementation = CongressmanListDTO.class)))
    })
    ResponseEntity<CongressmanDetailDTO> detail(Pageable pageable, @RequestParam Long size, @RequestParam Long cursor, @PathVariable(name = "id") String congressionmanId, @LoginId Long loginId);
}
