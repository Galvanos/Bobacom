package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.AddCreditReq;
import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.UtenteDTO;

public interface IUtenteService {

	/**
	 * Create senza alcun vincolo, con controlli solo per funzionalità minima
	 * @param req la richiesta di creazione
	 * @throws Exception in caso di errore
	 */
	void create(UtenteReq req) throws Exception;
	/**
	 * Create da invocare quando un utente registra sé stesso, vengono fatti dei controlli
	 * ed impostati dei valori (ad esempio il credito iniziale e il ruolo come utente)
	 * @param req la richiesta di creazione utente
	 * @throws Exception in caso di errore
	 */
	void createByUser(UtenteReq req) throws Exception;
	/**
	 * Aggiornamento senza vincoli, da usare per gli admin
	 * @param req la richiesta con l'update
	 * @throws Exception in caso di errori
	 */
	void update(UtenteReq req) throws Exception;
	/**
	 * Aggiornamento da parte dell'utente, ad esempio non consente di aggiornare il credito
	 * @param req la richiesta con l'aggiornamento
	 * @throws Exception in caso di errori
	 */
	void updateByUser(UtenteReq req) throws Exception;
	UtenteDTO getById(Integer id) throws Exception;
	List<UtenteDTO> list() throws Exception;
	void delete(Integer id) throws Exception;
	
	/**
	 * Funzione richiamabile da admin per aumentare il credito
	 * @param addCredReq
	 * @return
	 * @throws Exception
	 */
	UtenteDTO addCredit(AddCreditReq addCredReq) throws Exception;
	UtenteDTO addCreditByUser(AddCreditReq addCreditReq) throws Exception;
}
