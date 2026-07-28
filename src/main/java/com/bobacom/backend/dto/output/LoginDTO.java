package com.bobacom.backend.dto.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class LoginDTO {
	 private String accessToken;
	 private String tokenType;
}
