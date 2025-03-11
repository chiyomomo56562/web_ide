package com.web_ide.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class JwtAuthenticationResponseDto {
	private String accessToken;
	private String refreshToken;
}
