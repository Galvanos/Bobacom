package com.bobacom.backend.dto.input;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginReq {
	@NotNull (groups = ValidationGroups.Create.class , message ="login_invalid")
	private String username;
	@NotNull (groups = ValidationGroups.Create.class , message ="login_invalid")
	private String password;

}
