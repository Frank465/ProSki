package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Response.AttrezzaturaDisponibileResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.PrenotazioneResponse;
import com.ingegneriadelsoftware.ProSki.Model.Prenotazione;
import com.ingegneriadelsoftware.ProSki.Model.Sci;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Repository.PrenotazioneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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


    /**
     * Il metodo
     * @param prenotazione
     * @return
     */
    public PrenotazioneResponse creaPrenotazione(Prenotazione prenotazione) {
        //Ritorna la lista di sci e snowboard disponibili del rifornitore rifornitore
        AttrezzaturaDisponibileResponse attrezzaturaDisponibile =
                rifornitoreService.getAttrezzaturaDisponibile(prenotazione.getRifornitore());

        if(attrezzaturaDisponibile.getSciList().isEmpty() && attrezzaturaDisponibile.getSnowboardList().isEmpty())
            throw new IllegalStateException("Attrezzature non disponibili");

        List<Snowboard> prenotaSnowboards = new ArrayList<>();
        List<Sci> prenotaSci = new ArrayList<>();

        //Controlla che gli sci e gli snowboards indicati siano tra quelli disponibili del rifornitore
        prenotazione.getSnowboardprenotati()
                .stream()
                .filter(cur -> {
                    if(attrezzaturaDisponibile.getSnowboardList().contains(cur)) {  //Filtra solo gli snowboard che sono stati selezionati dall'utente
                        prenotaSnowboards.add(cur);
                        return true;
                    }
                    else
                        throw new IllegalStateException("Uno o più elementi non sono presenti, prenotazione annullata");
                })
                .collect(Collectors.toList());

        prenotazione.getSciPrenotati()
                .stream()
                .filter(cur -> {
                    if(attrezzaturaDisponibile.getSciList().contains(cur)) {  //Filtra solo gli sci che sono stati selezionati dall'utente
                        prenotaSci.add(cur);
                        return true;
                    }
                    else
                        throw new IllegalStateException("Uno o più elementi non sono presenti, prenotazione annullata");
                })
                .collect(Collectors.toList());

        //Vengono prenotati gli sci/snowboards
        sciService.prenotaSci(prenotaSci);
        snowboardService.prenotaSnowboards(prenotaSnowboards);

        prenotazioneRepository.save(prenotazione);

        return PrenotazioneResponse
                .builder()
                .nomeUtente(prenotazione.getUtente().getNome())
                .nomeRifornitore(prenotazione.getRifornitore().getNome())
                .dataPrenotazione(prenotazione.getDataInizio().toString())
                .dataDeposito(prenotazione.getDataFine().toString())
                .listaSci(prenotaSci)
                .listaSnowboards(prenotaSnowboards)
                .build();
    }


}
