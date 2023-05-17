package com.ingegneriadelsoftware.ProSki.DTO;

import com.ingegneriadelsoftware.ProSki.DTO.Request.*;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LezioneResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.PrenotazioneResponse;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Il DTOManager recupera tutte le request provenienti dal client e crea delle classi java utilizzabili
 */
@Component
@RequiredArgsConstructor
public class DTOManager {

    public static Localita getLocalitaByLocalitaRequest(LocalitaRequest request) {
        Localita localita = new Localita();
        localita.setNome(request.getNome());
        localita.setPrezzoAbbonamento(request.getPrezzoAbbonamento());
        return localita;
    }

    public static Maestro getMaestroByMaestroRequest(MaestroRequest request) {
        Maestro maestro = new Maestro();
        maestro.setNome(request.getNome());
        maestro.setCognome(request.getCognome());
        maestro.setEmail(request.getEmail());
        maestro.setSpecialita(request.getSpecialita());
        return maestro;
    }

    public static Rifornitore getRifornitoreByRifornitoreRequest(RifornitoreRequest request) {
        Rifornitore rifornitore = new Rifornitore();
        rifornitore.setNome(request.getNome());
        rifornitore.setEmail(request.getEmail());
        rifornitore.setLocalita(new Localita(request.getLocalita()));
        return rifornitore;
    }

    public static PrenotazioneResponse toPrenotazioneResponseByPrenotazione(Prenotazione prenotazione) {
        return PrenotazioneResponse
                .builder()
                .nomeUtente(prenotazione.getUtente().getNome())
                .nomeRifornitore(prenotazione.getRifornitore().getNome())
                .dataPrenotazione(prenotazione.getDataInizio().toString())
                .dataDeposito(prenotazione.getDataFine().toString())
                .listaSnowboards(prenotazione.getSnowboardPrenotati().toString())
                .listaSci(prenotazione.getSciPrenotati().toString())
                .build();
    }

    public static LezioneResponse toLezioneResponseByLezione(Lezione lezione) {
        return LezioneResponse
                .builder()
                .idLezione(lezione.getId())
                .maestro(lezione.getMaestro().getNome())
                .inizioLezione(lezione.getInizioLezione().toString())
                .fineLezione(lezione.getFineLezione().toString())
                .build();
    }

}
