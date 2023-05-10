package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Response.AttrezzaturaDisponibileResponse;
import com.ingegneriadelsoftware.ProSki.Model.Prenotazione;
import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import com.ingegneriadelsoftware.ProSki.Repository.PrenotazioneRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiPredicate;


/**
 * La classe contiene la logica e tutti i metodi necessari che sono interesasti alla prenotazione di attrezature
 */
@Service
@RequiredArgsConstructor
public class PrenotazioneService {
    private final RifornitoreService rifornitoreService;
    private final SciService sciService;
    private final SnowboardService snowboardService;
    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteService utenteService;


    /**
     * Il metodo crea una prenotazione solo nel caso in cui, per un determinato rifornitore,
     * tutti gli elementi della lista delle attrezzature siano disponibili
     * @param prenotazione
     * @return PrenotazioneResponse
     */
    @Transactional
    public Prenotazione creaPrenotazione(Prenotazione prenotazione) throws IllegalStateException {
        //Controllo rifornitore
        Rifornitore rifornitore = rifornitoreService.getRifornitoreByEmail(prenotazione.getRifornitore().getEmail());
        prenotazione.setRifornitore(rifornitore);
        //Controllo Utente
        Utente utente = (Utente) utenteService.loadUserByUsername(prenotazione.getUtente().getEmail());
        prenotazione.setUtente(utente);
        //Controllo date
        if(errorDataPrenotazione(prenotazione.getDataInizio(), prenotazione.getDataFine()))
            throw new DateTimeException("Errore nelle date della prenotazione, prenotazione fallita");

        //Ritorna la lista di sci e snowboard disponibili del rifornitore
        AttrezzaturaDisponibileResponse attrezzaturaDisponibile =
                rifornitoreService.getAttrezzaturaDisponibile(rifornitore.getRifornitoreId());

        //Controlla se ci sono attrezzature non disponibili, con una data di fine prenotazione scaduta
        Iterable prenotazioni = prenotazioneRepository.findAll();
        updateAttrezzature(prenotazioni);

        if(attrezzaturaDisponibile.getSciList().isEmpty() && attrezzaturaDisponibile.getSnowboardList().isEmpty())
            throw new IllegalStateException("Attrezzature non disponibili");

        //Controlla che gli sci e gli snowboards indicati siano tra quelli disponibili del rifornitore
        BiPredicate<Integer, List<Integer>> attrezzaturaComune = (attrezzatura, list)-> {
            if(list.contains(attrezzatura)){
                return true;
            }
            else
                throw new IllegalStateException("Uno o più elementi non sono presenti, prenotazione annullata");
        };

        //Richiama la funzione attrezzaturaComune e fa il controllo se è presente lo snowboard
        prenotazione.getSnowboardPrenotati()
                .stream()
                .filter(cur ->
                        attrezzaturaComune.test(cur.getId(), attrezzaturaDisponibile.getSnowboardList()))
                .toList();

        //Come sopra solo che con gli sci
        prenotazione.getSciPrenotati()
                .stream()
                .filter(cur -> attrezzaturaComune.test(cur.getId(), attrezzaturaDisponibile.getSciList()))
                .toList();

        //Vengono prenotati gli sci/snowboards
        sciService.setEnableSci(prenotazione.getSciPrenotati(), false);
        snowboardService.setEnableSnowboards(prenotazione.getSnowboardPrenotati(), false);

        prenotazioneRepository.save(prenotazione);

        return prenotazione;
    }

    /**
     * Il metodo prende tutte le prenotazioni e contralla per ognuna la sua data di fine,
     * se è scaduta i flag delle attrezzature venogno messi tutti a true
     * @param prenotazioni
     */
    private void updateAttrezzature(Iterable prenotazioni) {
        for (Prenotazione pre : (Iterable<Prenotazione>) prenotazioni) {
            //Se la prenotazione è scaduta gli sci/snowboards diventano disponibili
            if (prenotazioneScaduta(pre)) {
                sciService.setEnableSci(pre.getSciPrenotati(), true);
                snowboardService.setEnableSnowboards(pre.getSnowboardPrenotati(), true);
            }
        }
    }

    /**
     * Controllo scadenza prenotazione
     * @param prenotazione
     * @return
     */
    private boolean prenotazioneScaduta(Prenotazione prenotazione) {
        return prenotazione.getDataFine().isBefore(LocalDate.now());
    }

    /**
     * Controlla se la data di inizio e prima di quella di fine e che la prenotazione avvenga prima di oggi
     * @param inizio
     * @param fine
     * @return
     */
    private boolean errorDataPrenotazione(LocalDate inizio, LocalDate fine) {
        return !inizio.isBefore(fine) || inizio.isBefore(LocalDate.now());
    }





}
