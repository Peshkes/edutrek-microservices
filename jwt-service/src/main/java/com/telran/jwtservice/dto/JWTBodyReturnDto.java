package com.telran.jwtservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTBodyReturnDto {
    String accessToken;
    String refreshToken;
}
