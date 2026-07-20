package com.bobacom.backend.dto.input;

import java.math.BigDecimal;
import java.util.Set;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdineRequest {
	@NotNull(groups = ValidationGroups.Update.class, message = "id ordine non fornito")
    private Integer id;
    @NotNull(groups = ValidationGroups.Create.class, message = "user id non fornito")
	private Integer utente_id;
    @NotNull(groups = ValidationGroups.Create.class, message = "prezzo totale non fornito")
	private BigDecimal prezzoTotale;
    @NotNull(groups = ValidationGroups.Create.class, message = "stato ordine non fornito")
    @NotNull(groups = ValidationGroups.Update.class, message = "stato ordine non fornito")
	private String status;
	@NotNull(groups = ValidationGroups.Create.class, message = "indirizzo non fornito")
	private String indirizzoDestinazione;
}
