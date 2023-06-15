package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.ReservationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.SkiDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.SnowboardDTO;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.ReservationRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SkiRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SnowboardRepository;
import com.ingegneriadelsoftware.ProSki.Repository.UserRepository;
import com.ingegneriadelsoftware.ProSki.Security.JwtUtils;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;


/**
 * La classe contiene la logica e tutti i metodi necessari che sono interesasti alla prenotazione di attrezature
 */
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final VendorService vendorService;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    /**
     * Il metodo crea una prenotazione solo nel caso in cui, per un determinato rifornitore,
     * tutti gli elementi della lista delle attrezzature siano disponibili
     * @param reservationRequest
     * @return PrenotazioneResponse
     */
    @Transactional
    public Reservation createReservation(ReservationRequest reservationRequest, HttpServletRequest servletRequest) throws DateTimeException {
        //Prendo l'email dal token presente nella ServletRequest e da questo ricavo l'utente che sta effettuando la prenotazione
        User user = Utils.getUserFromHeader(servletRequest, userRepository, jwtUtils);
        System.out.println(user.getUserId());
        //Conversione Date da stringa a LocalDate
        LocalDate startDateReservation = Utils.formatterData(reservationRequest.getStartDate());
        LocalDate endDateReservation = Utils.formatterData(reservationRequest.getEndDate());
        //Controllo rifornitore
        Vendor vendor = vendorService.getVendorByEmail(reservationRequest.getVendorEmail());
        //Ritorna la lista di sci e snowboard (entrambi sono DTO, hanno solo id e misura) disponibili del rifornitore
        EquipmentAvailableResponse equipmentAvailable = vendorService.getEquipmentAvailableForDate(vendor.getEmail(), startDateReservation, endDateReservation);
        if(equipmentAvailable.getSkisList().isEmpty() && equipmentAvailable.getSnowboardsList().isEmpty())
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
        //Filtra la lista mantenendo solo gli Id degli sky utilizzando gli stream
        List<Integer> listIdSnowboard = equipmentAvailable.getSnowboardsList().stream().map(SnowboardDTO::getId).collect(Collectors.toList());
        reservationRequest.getSnowboardsList().stream().filter(cur -> attrezzaturaComune.test(cur.getId(), listIdSnowboard)).toList();
        //Come sopra solo che con gli sci
        List<Integer> listIdSky = equipmentAvailable.getSkisList().stream().map(SkiDTO::getId).collect(Collectors.toList());
        reservationRequest.getSkisList().stream().filter(cur -> attrezzaturaComune.test(cur.getId(), listIdSky)).toList();

        Reservation newReservation = new Reservation(
                user,
                vendor,
                reservationRequest.getSkisList(),
                reservationRequest.getSnowboardsList(),
                startDateReservation,
                endDateReservation
        );
        reservationRepository.save(newReservation);
        return newReservation;
    }
}
