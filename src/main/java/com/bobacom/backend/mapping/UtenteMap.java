package com.bobacom.backend.mapping;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.bobacom.backend.dto.output.OrdineDTO;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.model.Ordine;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.utilities.DateOperations;

public class UtenteMap {

	/**
	 * Crea un {@link UtenteDTO} a partire dall'{@link Utente} , per {@link Utente#getOrdini()} si procede inserendoli 
	 * in {@link UtenteDTO#setOrdini(List)} in ordine decrescente di creazione, dedotto ordinando in ordine decrescente 
	 * di data e a parità di data, usando l'id che è autoincrementante, nella list di ordini non viene mappato l'utente 
	 * internamente dato che sarebbe questo stesso utente
	 * @param storedUser {@link Utente} da mappare
	 * @return  una istanza di {@link UtenteDTO} che mappa l'utente
	 */
	public static UtenteDTO buildUtenteDTO(Utente storedUser) {
		Set<Ordine> ordini = storedUser.getOrdini();
		//comparatore ordini, vengono messi in ordine decrescente, quindi si guarda la data di creazione e a parità di data di creazione si guarda l'id che è autoincrementante
		Comparator<Ordine> comparatorOrdini = Comparator.comparing(Ordine::getDataCreazione)
				.thenComparing(Comparator.comparing(Ordine::getId)).reversed();
		
		List<OrdineDTO> ordiniDTOList = ordini.stream().sorted(comparatorOrdini).map(t -> OrdineDTO.builder()
				.dataCreazione(DateOperations.dateToString(t.getDataCreazione()))
				.id(t.getId())
				 .indirizzoDestinazione(t.getIndirizzoDestinazione())
				 .prezzoTotale(t.getPrezzoTotale())
				 .status(t.getStatus().name())
				 //utente non viene passato perché sarebbe l'utente stesso che si sta mappando ora
				 .build()).toList();
		return UtenteDTO.builder()
				.credito(storedUser.getCredito())
				.email(storedUser.getEmail())
				.id(storedUser.getId())
				.indirizzo(storedUser.getIndirizzo())
				.password(storedUser.getPassword())//hash della password, nel rest controller va annullata
				.ruolo(storedUser.getRuolo())
				.ordini(ordiniDTOList)
				.username(storedUser.getUsername())
				.build();
	}

	/**
	 * Crea una {@link List} di {@link UtenteDTO} usando {@link #buildUtenteDTO(Utente)} 
	 * @param users {@link Collection} di {@link Utente}
	 * @return una {@link List} di {@link UtenteDTO}
	 */
	public static List<UtenteDTO> buildUtenteDTOList(Collection<Utente> users) {
		return users.stream().map(storedUser -> 
			 buildUtenteDTO(storedUser)).toList();
	}

}
