package com.ingegneriadelsoftware.ProSki.DTO;

import com.ingegneriadelsoftware.ProSki.DTO.Request.LocalitaRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MaestroRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.PrenotazioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.RifornitoreRequest;
import com.ingegneriadelsoftware.ProSki.Model.Localita;
import com.ingegneriadelsoftware.ProSki.Model.Maestro;
import com.ingegneriadelsoftware.ProSki.Model.Prenotazione;
import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Il DTOManager recupera tutte le request provenienti dal client e crea delle classi java utilizzabili
 */

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
        rifornitore.setLocalita(request.getLocalita());
        return rifornitore;
    }

    public static Prenotazione getPrenotazioneByPrenotazioneRequest(PrenotazioneRequest request) {
        Prenotazione prenotazione = new Prenotazione();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        prenotazione.setUtente(request.getUtente());
        prenotazione.setRifornitore(request.getRifornitore());
        prenotazione.setDataInizio(LocalDate.parse(request.getDataInizio(), formatter));
        prenotazione.setDataFine(LocalDate.parse(request.getDataFine(), formatter));
        prenotazione.setSnowboardprenotati(request.getSnowboards());
        prenotazione.setSciPrenotati(request.getSci());
        return prenotazione;
    }
}
