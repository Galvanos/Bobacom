package com.bobacom.backend.service.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.bobacom.backend.dto.input.AddCreditReq;
import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.UtenteDTO;

public interface IUtenteService {

	
	
	/**
	 * Create senza alcun vincolo, con controlli solo per funzionalità minima
	 * @param req la richiesta di creazione utente
	 * @return l'utente appena creato
	 * @throws Exception in caso di errore
	 */
	UtenteDTO create(UtenteReq req) throws Exception;
	/**
	 * Create da invocare quando un utente registra sé stesso, vengono fatti dei controlli
	 * ed impostati dei valori (ad esempio il credito iniziale e il ruolo come utente)
	 * @param req la richiesta di creazione utente
	 * @return l'utente appena creato
	 * @return 
	 * @throws Exception in caso di errore
	 */
	UtenteDTO createByUser(UtenteReq req) throws Exception;
	
	/**
	 * Aggiornamento senza vincoli, da usare per gli admin
	 * @param req la richiesta con l'update
	 * @return l'utente appena aggiornato
	 * @throws Exception in caso di errori
	 */
	UtenteDTO update(UtenteReq req) throws Exception;
	/**
	 * Aggiornamento da parte dell'utente, ad esempio non consente di aggiornare il credito,
	 * verifica che l'utente che aggiorna sia l'utente stesso.
	 * <br/>
	 * Per non invalidare il JWT, l'utente non può cambiare lo username, può essere fatto da amministratore purché non sia
	 * l'amministratore stesso
	 * @param req la richiesta con l'aggiornamento
	 * @return l'utente appena aggiornato
	 * @throws Exception in caso di errori
	 */
	UtenteDTO updateByUser(UtenteReq req) throws Exception;
	UtenteDTO getById(Integer id) throws Exception;
	UtenteDTO getByUsername(String username) throws Exception;
	/**
	 * Informazioni riguardo l'utente accessibili solo all'utente stesso,
	 * verifica che il richiedente sia lo stesso utente collegato
	 * @param id id dell'utente cercato
	 * @return i dati dell'utente
	 * @throws Exception in caso di errori
	 */
	UtenteDTO getByIdByUser(Integer id) throws Exception;
	/**
	 * Informazioni riguardo l'utente accessibili solo all'utente stesso,
	 * verifica che il richiedente sia lo stesso utente collegato
	 * @param username nome utente cercato
	 * @return i dati dell'utente
	 * @throws Exception in caso di errori
	 */
	UtenteDTO getByUsernameByUser(String username) throws Exception;
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
