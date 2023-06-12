package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.ReservationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.ReservationRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SkyRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SnowboardRepository;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
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
public class ReservationService {
    private final VendorService vendorService;
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final SkyRepository skyRepository;
    private final SnowboardRepository snowboardRepository;

    /**
     * Il metodo crea una prenotazione solo nel caso in cui, per un determinato rifornitore,
     * tutti gli elementi della lista delle attrezzature siano disponibili
     * @param reservationRequest
     * @return PrenotazioneResponse
     */
    @Transactional
    public Reservation createReservation(ReservationRequest reservationRequest, HttpServletRequest servletRequest) throws IllegalStateException {
        //Prendo l'email dal token presente nella ServletRequest e da questo ricavo l'utente che sta effettuando la prenotazione
        String userEmail = jwtService.findEmailUtenteByHttpServletRequest(servletRequest);

        //Conversione Date da stringa a LocalDate
        LocalDate startDateReservation = Utils.formatterData(reservationRequest.getStartDate());
        LocalDate endDateReservation = Utils.formatterData(reservationRequest.getEndDate());

        //Controllo rifornitore
        Vendor vendor = vendorService.getVendorByEmail(reservationRequest.getVendorEmail());

        //Controllo Utente
        User user = (User) userService.loadUserByUsername(userEmail);

        //Controllo date
        if(errorDateReservation(startDateReservation, endDateReservation))
            throw new DateTimeException("Errore nelle date della prenotazione, prenotazione fallita");

        //Ritorna la lista di sci e snowboard disponibili del rifornitore
        EquipmentAvailableResponse equipmentAvailable = vendorService.getEquipmentAvailable(vendor.getVendorId());

        //Controlla se ci sono attrezzature non disponibili, con una data di fine prenotazione scaduta
        Iterable<Reservation> prenotazioni = reservationRepository.findAll();
        updateAttrezzature(prenotazioni);

        if(equipmentAvailable.getSkyList().isEmpty() && equipmentAvailable.getSnowboardList().isEmpty())
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
        reservationRequest.getSnowboardsList()
                .stream()
                .filter(cur ->
                        attrezzaturaComune.test(cur.getId(), equipmentAvailable.getSnowboardList()))
                .toList();

        //Come sopra solo che con gli sci
        reservationRequest.getSkyList()
                .stream()
                .filter(cur -> attrezzaturaComune.test(cur.getId(), equipmentAvailable.getSkyList()))
                .toList();

        //Vengono prenotati gli sci/snowboards
        setEnableSci(reservationRequest.getSkyList(), false);
        setEnableSnowboards(reservationRequest.getSnowboardsList(), false);

        Reservation newReservation = new Reservation(
                user,
                vendor,
                reservationRequest.getSkyList(),
                reservationRequest.getSnowboardsList(),
                startDateReservation,
                endDateReservation
        );

        reservationRepository.save(newReservation);

        return newReservation;
    }

    /**
     * Il metodo prende tutte le prenotazioni e contralla per ognuna la sua data di fine,
     * se è scaduta i flag delle attrezzature venogno messi tutti a true
     * @param reservations
     */
    private void updateAttrezzature(Iterable<Reservation> reservations) {
        for (Reservation pre : reservations) {
            //Se la prenotazione è scaduta gli sci/snowboards diventano disponibili
            if (prenotazioneScaduta(pre)) {
                setEnableSci(pre.getSkyReserved(), true);
                setEnableSnowboards(pre.getSnowboardReserved(), true);
            }
        }
    }

    /**
     * Controllo scadenza prenotazione
     * @param reservation
     * @return
     */
    private boolean prenotazioneScaduta(Reservation reservation) {
        return reservation.getEndDate().isBefore(LocalDate.now());
    }

    /**
     * Controlla se la data di inizio e prima di quella di fine e che la prenotazione avvenga prima di oggi
     * @param start
     * @param end
     * @return
     */
    private boolean errorDateReservation(LocalDate start, LocalDate end) {
        return !start.isBefore(end) || start.isBefore(LocalDate.now());
    }

    /**
     * Il metodo data una lista di sci ed un valore, aggiorna per ogni sci il corrispondente valore di enable
     * @param prenotaSky
     */
    private void setEnableSci(List<Sky> prenotaSky, boolean enable) {
        prenotaSky.forEach(sci -> {
            skyRepository.setEnable(enable, sci.getId());
        });
    }
    /**
     * Il metodo data una lista di snowboards ed un valore, aggiorna per ogni snowboard il corrispondente valore di enable
     * @param prenotaSnowboards value
     */
    private void setEnableSnowboards(List<Snowboard> prenotaSnowboards, boolean enable) {
        prenotaSnowboards.forEach(snowboard -> {
            snowboardRepository.setEnable(snowboard.getId(), enable);
        });
    }
}
