package com.bobacom.backend.dto.input;

import java.math.BigDecimal;

import com.bobacom.backend.dto.input.validation.ValidationGroups;
import com.bobacom.backend.enums.Ruolo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UtenteReq {

	@NotNull(groups = ValidationGroups.Update.class, message = "id utente non fornito"  )
	private Integer id;
	@NotNull(groups = ValidationGroups.Create.class, message = "username non fornito"  )
	private String username;
	@NotNull(groups = ValidationGroups.Create.class, message = "email non fornita"  )
	@Email
	private String email;
	@NotNull(groups = ValidationGroups.Create.class, message = "password non fornita"  )
	private String password;
	private Ruolo ruolo;//in caso di ruolo null si impone user
	private BigDecimal credito;//credito non obbligatorio in fase di creazione
	private String indirizzo;//indirizzo non obbligatorio
}
