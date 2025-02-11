package com.guenbon.siso.dto.auth.naver;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NaverToken {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String error;
    private Integer errorDescription;
}
