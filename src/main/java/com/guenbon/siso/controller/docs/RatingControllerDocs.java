package com.guenbon.siso.controller.docs;

import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import com.guenbon.siso.support.annotation.LoginId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "평가 API")
public interface RatingControllerDocs {

    @Operation(summary = "평가 작성 요청", description = "평가 작성 후 홈페이지로 리다이렉트 할 지 작성한 평가 상세보기를 해줄건지 결정 필요")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "국회의원 평가 작성 후 국회의원 상세보기로 리다이렉트",
                    content = @Content())
    })
    void create(@LoginId Long loginId, @RequestBody RatingWriteDTO ratingWriteDTO, HttpServletResponse response) throws IOException;
}
