package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.CommentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorEquipmentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.CommentDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
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
    private final VendorCommentRepository vendorCommentRepository;

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
     * Il metodo ritorna le attrezzature (sci e snowboard) disponibili di un rifornitore per un intervallo di date
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
        //sono in tutto 8 casi poichè le date che si incrociano sono 4
        List<Reservation> reservationVendorForDate = reservationByVendor.stream().filter(cur ->
                   start.isEqual(cur.getStartDate())
                           || end.isEqual(cur.getEndDate())
                           || start.isEqual(cur.getEndDate())
                           || end.isEqual(cur.getStartDate())
                || (start.isAfter(cur.getStartDate()) && start.isBefore(cur.getEndDate()))
                || (cur.getStartDate().isAfter(start) && cur.getStartDate().isBefore(end))
                || (end.isAfter(cur.getStartDate()) && end.isBefore(cur.getEndDate()))
                || (cur.getEndDate().isAfter(start) && cur.getEndDate().isBefore(end))

        ).toList();
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
        skiAvaiable.forEach(cur -> snowboardsDTO.add(SnowboardDTO.builder().id(cur.getId()).measure(cur.getMeasure()).build()));
        //Creo una lista di sci dto con i parametri da tornare al client
        List<SkiDTO> skisDTO = new ArrayList<>();
        skiAvaiable.forEach(cur -> skisDTO.add(SkiDTO.builder().id(cur.getId()).measure(cur.getMeasure()).build()));
        return EquipmentAvailableResponse.builder()
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

    /**
     * Un utente iscritto può creare dei messaggi nel forum solo se ha prenotato almeno una volta delle attrezzature dal
     * rifornitore indicato. La creazione dei messaggi avviene tramite l'utilizzo del pattern Strategy
     * @param request
     * @param httpServletRequest
     * @return
     */
    public String createMessage(MessageRequest request, HttpServletRequest httpServletRequest) {
        //Controllo ed estrapolazione utente dal Context di Security
        User user = Utils.getUserFromHeader(httpServletRequest, userRepository, jwtUtils);
        //Controllo che l'utente abbia effettuato una prenotazione dal rifornitore
        controlUserReservation(request.getUsername(), user);
        //Set della strategia di publicazione del messaggio
        context.setPublishingStrategy(new ConcreteStrategyVendor(vendorRepository, vendorMessageRepository));
        //Eseguo la strategia di pubblicazione del messaggio per il rifornitore
        return context.executeMessageStrategy(request.getUsername(), user, request.getMessage());
    }

    /**
     * Un utente iscritto può commentare un messaggio presente solo quando ha effettuato almeno una volta la prenotazione
     * delle attrezzature dal rifornitore indicato. La creazione dei messaggi avviene tramite l'utilizzo del pattern Strategy
     * @param request
     * @param httpServletRequest
     * @return
     */
    public String createCommentToMessage(CommentRequest request, HttpServletRequest httpServletRequest) {
        //Controllo ed estrapolazione utente dal Context di Security
        User user = Utils.getUserFromHeader(httpServletRequest, userRepository, jwtUtils);
        //Controllo che l'utente abbia effettuato una prenotazione dal rifornitore
        controlUserReservation(request.getUsername(), user);
        VendorMessage vendorMessage = vendorMessageRepository.findById(request.getIdMessage()).orElseThrow(()-> new IllegalStateException("Il messaggio indicato non esiste"));
        //Set della strategia di publicazione del commento
        context.setPublishingStrategy(new ConcreteStrategyVendor(vendorRepository, vendorMessageRepository, vendorCommentRepository));
        //Eseguo la strategia di pubblicazione del messaggio per il rifornitore
        return context.executeCommentStrategy(request.getIdMessage(), user, request.getComment());
    }

    /**
     * Controlla che l'utente che vuole inserire un messaggio o un commento abbia almeno una volta effettuato una prenotazione
     * dal rifornitore indicato
     * @param request
     * @param httpServletRequest
     */
    private void controlUserReservation(String username, User user) {
        //Controllo esistenza rifornitore e tutte le sue prenotazioni
        Vendor vendor = getVendorByEmail(username);
        List<Reservation> vendorReservations = vendor.getReservations();
        if(vendorReservations.isEmpty()) throw new IllegalStateException("Non sono presenti prenotazioni per questo rifornitore");
        //Filtro la lista per prenotazoni di un utente
        List<Reservation> userReservation = vendorReservations.stream().filter(cur-> cur.getUser().equals(user)).toList();
        if(userReservation.isEmpty()) throw new IllegalStateException("L'utente non ha mai effettuato prenotazioni a questo rifornitore " + vendor.getName());
    }

    /**
     * Dato un rifornitore vengono ritornati tutti i messaggi che gli utenti hanno pubblicato e i rispettivi commenti
     * @param idLocation
     * @return
     */
    public MessageResponse getAllMessage(Integer idRifornitore) {
        Vendor vendor = vendorRepository.findById(idRifornitore).orElseThrow(()-> new IllegalStateException("Il Rifornitore non esiste"));
        List<VendorMessage> vendorsMessage = vendorMessageRepository.findAllByVendor(vendor);

        List<MessageDTO> messageDTOS = new ArrayList<>();
        //Crea una commentDTO nella quale ci sono i dati importanti di tutti i commenti legati ad un messaggio
        //Prende tutti i messaggi di un rifornitore che vengono gestiti come DTO e vengono agigunti i relativi commenti per ogni messaggio
        vendorsMessage.forEach(cur -> {
            List<CommentDTO> commentDTOS = new ArrayList<>();
            cur.getVendorComments().forEach(elem -> {
                commentDTOS.add(CommentDTO.builder().id(elem.getCommentId()).user(elem.getUser().getEmail()).comment(elem.getComment()).build());
            });
            messageDTOS.add(MessageDTO.builder().idMessage(cur.getMessageId()).user(cur.getUser().getUserId()).message(cur.getMessage()).comments(commentDTOS).build());
        });
        return MessageResponse.builder().idLocation(idRifornitore).listMessage(messageDTOS).build();
    }

    /**
     *Il metodo elimina un messaggio e a cascata tutti i commenti che ad esso si riferiscono.
     * In questo caso si tratta di un messaggio creato da un utente sul forum di un rifornitore
     * Il compito è riservato all'admin del sistema
     * @param request
     * @return
     */
    public MessageDTO deleteMessage(MessageDTO request) {
        VendorMessage message = vendorMessageRepository.findById(request.getIdMessage())
                .orElseThrow(()->new IllegalStateException("Il messaggio cercato non esiste"));
        vendorMessageRepository.delete(message);
        return MessageDTO.builder()
                .idMessage(message.getMessageId())
                .message(message.getMessage())
                .user(message.getUser().getUserId())
                .build();
    }

    /**
     * Il metodo elimina una lista di commenti ritenuti in opportuni da parte dell'admin per un determinato messaggio
     * In questo caso si tratta di uno o più commenti che degli utenti fanno sul messaggio di un utente sul forum di un rifornitore
     * @param request
     * @return
     */
    public MessageDTO deleteComments(MessageDTO request) {
        if(request.getComments().isEmpty()) throw new IllegalStateException("Non ci sono commenti selezionati");
        VendorMessage message = vendorMessageRepository.findById(request.getIdMessage())
                .orElseThrow(()-> new EntityNotFoundException("Il messaggio indicato non esiste"));
        List<VendorComment> comments = new ArrayList<>();
        request.getComments().forEach(cur -> comments.add(vendorCommentRepository.findById(cur.getId())
                .orElseThrow(()-> new EntityNotFoundException("Un dei commenti indicati non esiste"))));
        vendorCommentRepository.deleteAll(comments);
        return MessageDTO.builder()
                .idMessage(message.getMessageId())
                .comments(request.getComments())
                .build();
    }
}
