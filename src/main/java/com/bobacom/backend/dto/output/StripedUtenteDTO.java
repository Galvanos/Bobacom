/**
 * 
 */
package com.bobacom.backend.dto.output;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
/**
 * Versione di {@link UtenteDTO} contenente anche i dati di pagamento per stripe
 */
public class StripedUtenteDTO extends UtenteDTO {

	private String clientSecret;

}
