package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorEquipmentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.SkiDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.SnowboardDTO;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.ConcreteStrategyVendor;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.Context;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.*;
import com.ingegneriadelsoftware.ProSki.Security.JwtUtils;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final LocationService locationService;
    private final SkiRepository skiRepository;
    private final SnowboardRepository snowboardRepository;
    private final Context context;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final ReservationRepository reservationRepository;
    private final VendorMessageRepository vendorMessageRepository;

    /**
     * Il metodo crea un rifornitore
     * @param request
     * @return String
     * @throws IllegalStateException
     * @throws EntityNotFoundException
     */
    public String insertVendor(VendorRequest request) throws EntityNotFoundException {
        //Controllo se il rifornitore è gia presente
        if(vendorRepository.findByEmail(request.getEmail()).isPresent()) throw new IllegalStateException("Rifornitore già presente");
        //Controllo se la localita del fornitore esiste
        Location location = locationService.getLocalitaByName(request.getLocation());
        Vendor newVendor = new Vendor(request.getName(), request.getEmail(), location);
        vendorRepository.save(newVendor);
        return "Rifornitore " + request.getName() + " è stato inserito correttamente";
    }

    /**
     * Il metodo ritorna le attrezzature (sci e snowboard) disponibili per una intervallo di date
     * @param IdRifornitore
     * @return
     */
    public EquipmentAvailableResponse getEquipmentAvailableForDate(String vendorEmail, LocalDate start, LocalDate end) throws DateTimeException{
        //Controllo esistenza del vendor
        Vendor vendor = getVendorByEmail(vendorEmail);
        //Controllo date
        if(start.isAfter(end) || start.isBefore(LocalDate.now()))
            throw new DateTimeException("Errore nelle date della prenotazione, prenotazione fallita");
        //Prende tutte le prenotazioni fatte per un rifornitore
        List<Reservation> reservationByVendor = reservationRepository.findAllByVendor(vendor);
        //Filtra tutte le prenotazioni che sono state effettuate dopo la data di nuova prenotazione e prima della data di fine nuova prenotazione
        List<Reservation> reservationVendorForDate = reservationByVendor.stream().filter(cur -> !cur.getStartDate().isBefore(start) && !cur.getEndDate().isAfter(end)).toList();
        //Prendo tutti gli sci e gli snowboard del rifornitore
        List<Ski> skiVendor = skiRepository.findByVendor(vendor);
        List<Snowboard> snowboardsVendor = snowboardRepository.findByVendor(vendor);
        //Prendo tutti gli sci e gli snowboard disponibili
        List<Ski> skiAvaiable = skiVendor;
        List<Snowboard> snowboardAvailable = snowboardsVendor;
        //Filtro solo gli sci e gli snowboard del rifornitore che non sono in nessuna delle prenotazioni
        reservationVendorForDate.forEach( cur -> {
            cur.getSkiReserved().forEach( elem -> {
                skiAvaiable.remove(elem);
            });
            cur.getSnowboardReserved().forEach( elem -> {
                snowboardAvailable.remove(elem);
            });
        });
        //Creo una lista di snowboard dto con i parametri da tornare al client
        List<SnowboardDTO> snowboardsDTO = new ArrayList<>();
        skiAvaiable.forEach(cur -> snowboardsDTO.add(SnowboardDTO.builder()
                .id(cur.getId())
                .measure(cur.getMeasure()).build()));

        //Creo una lista di sci dto con i parametri da tornare al client
        List<SkiDTO> skisDTO = new ArrayList<>();
        skiAvaiable.forEach(cur -> skisDTO.add(SkiDTO.builder()
                .id(cur.getId())
                .measure(cur.getMeasure()).build()));

        return EquipmentAvailableResponse
                .builder()
                .vendorEmail(vendor.getEmail())
                .snowboardsList(snowboardsDTO)
                .skisList(skisDTO)
                .build();
    }

    /**
     * Cerca rifornitore tramite email
     * @param email
     * @return
     * @throws IllegalStateException
     */
    public Vendor getVendorByEmail(String email) {
        return vendorRepository.findByEmail(email).
                orElseThrow(()-> new IllegalStateException("Il rifornitore non è stato trovato"));
    }


    /**
     * Le attrezzature che arrivano dalla request vengono inserite nell'inventario di un rifornitore
     * @param request
     * @return
     */
    public String createEquipment(VendorEquipmentRequest request) {
        Vendor vendor = getVendorByEmail(request.getVendorEmail());
        insertSky(request.getSki(), vendor);
        insertSnowboards(request.getSnowboards(), vendor);
        return "Attrezzature inserite correttamente";
    }

    /**
     * Per ogni sci viene settato il rifornitore e salvato nel DB lo sci
     * @param ski
     */
    private void insertSky(List<Ski> ski, Vendor rif) {
        ski.forEach(cur -> {cur.setVendor(rif);
            skiRepository.save(cur);});
    }

    /**
     * Per ogni elemento viene settato il rifornitore e salvato nel DB
     * @param snowboards
     * @param vendor
     */
    private void insertSnowboards(List<Snowboard> snowboards, Vendor vendor) {
        snowboards.forEach(cur -> {cur.setVendor(vendor); snowboardRepository.save(cur);});
    }

    public String createMessage(MessageRequest request, HttpServletRequest httpServletRequest) {
        //Controllo ed estrapolazione utente dal Context di Security
        User user = Utils.getUserFromHeader(httpServletRequest, userRepository, jwtUtils);
        //Controllo esistenza rifornitore e tutte le sue prenotazioni
        Vendor vendor = getVendorByEmail(request.getUsername());
        List<Reservation> vendorReservations = vendor.getReservations();
        if(vendorReservations.isEmpty()) throw new IllegalStateException("Non sono presenti prenotazioni per questo rifornitore");
        List<Reservation> userReservation = vendorReservations.stream().filter(cur-> cur.getUser().equals(user)).toList();
        if(userReservation.isEmpty()) throw new IllegalStateException("L'utente non ha mai effettuato prenotazioni a questo rifornitore " + vendor.getName());

        context.setPublishingStrategy(new ConcreteStrategyVendor(vendorRepository, vendorMessageRepository));
        return context.executeStrategy(request.getUsername(), user, request.getMessage());
    }
}
