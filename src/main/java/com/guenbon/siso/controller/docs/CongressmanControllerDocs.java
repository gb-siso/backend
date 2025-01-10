package com.guenbon.siso.controller.docs;

import com.guenbon.siso.dto.congressman.response.CongressmanDetailDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.support.annotation.LoginId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "국회의원 API")
public interface CongressmanControllerDocs {

    @Operation(summary = "국회의원 리스트 (홈페이지)", description = "정렬, 필터 쿼리파라미터 포함 요청")
    @Parameters(value = {
            @Parameter(name = "pageable", description = "page=3&size=10&sort=rating,DESC 와 같이 요청, 기본값 : page=0&size=20&sort=topicality,DESC"),
            @Parameter(name = "cursor", description = "무한스크롤 구현 위한 값 , 응답에 있는 값 그대로 사용하면 됨 (default : Long 최댓값 (최초 요청시))"),
            @Parameter(name = "party", description = "당으로 필터링하기 위한 당 id, default : 값 X"),
            @Parameter(name = "search", description = "검색어 (당 또는 이름)")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "국회의원 목록 (홈페이지)", content = @Content(schema = @Schema(implementation = CongressmanListDTO.class)))
    })
    ResponseEntity<CongressmanListDTO> congressmanList(
            @PageableDefault(page = 0, size = 20, sort = "topicality") Pageable pageable,
            @RequestParam String cursorId,
            @RequestParam(required = false) Double cursorRate,
            @RequestParam(required = false) String party,
            @RequestParam(required = false) String search);

    @Operation(summary = "국회의원 상세보기", description = "국회의원 상세보기 요청")
    @Parameters(value = {
            @Parameter(name = "pageable", description = "평가 정렬 방식, page=3&size=10&sort=likes 와 같이 요청, 기본값 : page=0&size=20&sort=topicality,DESC"),
            @Parameter(name = "cursor", description = "무한스크롤 구현 위한 값 , 응답에 있는 값 그대로 사용하면 됨 (default : Long 최댓값 (최초 요청시))"),
            @Parameter(name = "id", description = "조회 대상 국회의원 id"),
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "국회의원 상세보기", content = @Content(schema = @Schema(implementation = CongressmanDetailDTO.class)))
    })
    ResponseEntity<CongressmanDetailDTO> congressmanDetail(
            @PageableDefault(page = 0, size = 20, sort = "topicality") Pageable pageable,
            @RequestParam(required = false) Long cursor,
            @PathVariable(name = "id") String congressmanId,
            @LoginId Long loginId);
}
