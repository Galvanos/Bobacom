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
	/**
	 * Cancella un utente
	 * @param id id dell'utente da cancellare
	 * @return l'utente appena cancellato
	 * @throws Exception in caso di errori, tra cui utente inesistente
	 */
	UtenteDTO delete(Integer id) throws Exception;
	
	/**
	 * Funzione richiamabile da admin per aumentare il credito
	 * @param addCredReq richiesta per aggiungere il credito
	 * @return l'utente dopo che gli è stato aggiunto il credito
	 * @throws Exception  in caso di errori
	 */
	UtenteDTO addCredit(AddCreditReq addCredReq) throws Exception;
	/**
	 * Funzione richiamabile dall'utente per aggiungersi credito, 
	 * verifica che ad aggiungere credito sia l'utente stesso o un amministratore
	 * @param addCreditReq richiesta per aggiungere il credito
	 * @return l'utente dopo che gli è stato aggiunto il credito
	 * @throws Exception in caso di errori
	 */
	UtenteDTO addCreditByUser(AddCreditReq addCreditReq) throws Exception;
}
