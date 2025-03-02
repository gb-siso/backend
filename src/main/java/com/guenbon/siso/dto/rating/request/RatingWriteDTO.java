package com.guenbon.siso.dto.rating.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RatingWriteDTO {

    @NotBlank(message = "congressmanId는 필수입니다.")
    private String congressmanId;

    @Length(max = 100, message = "content는 100자 이하여야 합니다.")
    @NotBlank(message = "content는 필수입니다.")
    private String content;

    @DecimalMin(value = "0.0", inclusive = true, message = "rating은 0.0 이상이어야 합니다.")
    @DecimalMax(value = "10.0", inclusive = true, message = "rating은 10.0 이하여야 합니다.")
    @Digits(integer = 2, fraction = 1, message = "rating은 소수점 1자리까지 입력 가능합니다.")
    @NotNull(message = "rating는 필수입니다.")
    private Double rating;
}
