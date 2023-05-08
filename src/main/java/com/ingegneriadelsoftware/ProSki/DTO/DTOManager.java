package com.ingegneriadelsoftware.ProSki.DTO;

import com.ingegneriadelsoftware.ProSki.DTO.Request.LocalitaRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MaestroRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.PrenotazioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.RifornitoreRequest;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Il DTOManager recupera tutte le request provenienti dal client e crea delle classi java utilizzabili
 */
@Component
@RequiredArgsConstructor
public class DTOManager {
    private final JwtService jwtService;

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

    /**
     * Il metodo mappa una request su un oggetto di tipo prenotazione
     * @param request
     * @param servletRequest
     * @return
     */
    public Prenotazione getPrenotazioneByPrenotazioneRequest(PrenotazioneRequest request, HttpServletRequest servletRequest) {
        //Prendo l'email dal token presente nella ServletRequest e da questo ricavo l'utente che sta effettuando la prenotazione
        String authHeader = servletRequest.getHeader("Authorization");
        String token = authHeader.substring(7); //il token si trova a quella posizione dall'inizio di Header
        String email = jwtService.exctractUsername(token);
        //Formattazione delle date in ingresso
        Prenotazione prenotazione = new Prenotazione();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        //Creazione della prenotazione
        prenotazione.setUtente(new Utente(email));
        prenotazione.setRifornitore(new Rifornitore(request.getEmailRifornitore()));
        prenotazione.setDataInizio(LocalDate.parse(request.getDataInizio(), formatter));
        prenotazione.setDataFine(LocalDate.parse(request.getDataFine(), formatter));
        prenotazione.setSnowboardprenotati(request.getSnowboards());
        prenotazione.setSciPrenotati(request.getSci());
        return prenotazione;
    }
}
