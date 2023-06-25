package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.CardSkipassRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.CommentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LocationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LessonResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LocationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.CommentDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.ConcreteStrategyLocation;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.ConcreteStrategyVendor;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.Context;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.*;
import com.ingegneriadelsoftware.ProSki.Security.JwtUtils;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final CardSkipassRepository cardSkipassRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final BuySkipassRepository buySkipassRepository;
    private final Context context;
    private final LocationMessageRepository locationMessageRepository;
    private final LocationCommentRepository locationCommentRepository;



    public Location getLocalitaByName(String location) throws EntityNotFoundException {
        return locationRepository.findByName(location)
                .orElseThrow(() -> new EntityNotFoundException("Localita non trovata"));
    }

    /**
     *Il metodo crea una localita a partire da una request(DTO), i valori presenti delle date di inizio stagione
     * e fine stagione vengono parsate come LocalDate mentre gli orari di apertura degli impianti sono parsati come LocalTime
     * @param request
     * @return
     * @throws DateTimeException
     */
    public Location createLocation(LocationRequest request) throws DateTimeException {
        Optional<Location> location = locationRepository.findByName(request.getName());
        if(location.isPresent()) throw new IllegalStateException("località gia presente");
        // Parse e controllo date inizio e fine stagione
        LocalDate startOfSeason = Utils.formatterData(request.getStartOfSeason());
        LocalDate endOfSeason = Utils.formatterData(request.getEndOfSeason());
        if(!startOfSeason.isAfter(LocalDate.now()) || endOfSeason.isBefore(startOfSeason))
            throw new IllegalStateException("Date inserite non valide");
        //Parse e controllo orario apertura e chiusura impianti
        LocalTime openSkilift = Utils.formatterTime(request.getOpeningSkiLift());
        LocalTime closeSkilift = Utils.formatterTime(request.getClosingSkiLift());
        if(!openSkilift.isBefore(closeSkilift))
            throw new IllegalStateException("Errore inserimento orario apertura chiusura impianti");
        Location newLocation = new Location(
                request.getName(),
                request.getPriceSubscription(),
                endOfSeason,
                startOfSeason,
                openSkilift,
                closeSkilift
        );
        locationRepository.save(newLocation);
        return newLocation;
    }

    /**
     * Il metodo ritorna tutte le location create dall'admin
     * @return
     */
    public List<LocationResponse> getAllLocation() {
        Iterable<Location> locations = locationRepository.findAll();
        List<LocationResponse> lessonsResponse = new ArrayList<>();
        for (Location location : locations) {
            lessonsResponse.add(LocationResponse.builder()
                    .locationId(location.getLocationId())
                    .name(location.getName())
                    .startOfSeason(location.getStartOfSeason())
                    .endOfSeason(location.getEndOfSeason())
                    .priceSubscription(location.getPriceSubscription())
                    .openingSkiLift(location.getOpeningSkiLift())
                    .closingSkiLift(location.getClosingSkiLift())
                    .build());
        }
        return lessonsResponse;
    }

    /**
     * Creazione di skipass per una determinata località
     * @param request
     * @return
     */
    public String createSkipass(CardSkipassRequest request) {
         Optional<CardSkipass> skipass = cardSkipassRepository.findByCardCode(request.getCardCode());
         if(skipass.isPresent()) throw new IllegalStateException("La tessera è già presente");
         Location location = getLocalitaByName(request.getLocation());
         CardSkipass newCardSkipass = new CardSkipass(request.getCardCode(), location);
         cardSkipassRepository.save(newCardSkipass);
         return "Lo Skipass è stato aggiunto correttamente alla località " + location.getName();
    }

    /**
     * Un utente può pubblicare un messaggio per la località inserita solo se ha acquistato almeno uno skipass.
     * L'implementazione avviene tramite pattern Strategy
     * @param request
     * @param httpServletRequest
     * @return
     */
    public String createMessage(MessageRequest request, HttpServletRequest httpServletRequest)  {
        //Controllo esistenza utente
        User user = Utils.getUserFromHeader(httpServletRequest, userRepository, jwtUtils);
        controlUserLocation(request.getUsername(), user);
        //Set della strategia di publicazione del messaggio
        context.setPublishingStrategy(new ConcreteStrategyLocation(locationRepository, locationMessageRepository));
        //Eseguo la strategia di pubblicazione del messaggio per la località
        return context.executeMessageStrategy(request.getUsername(), user, request.getMessage());
    }

    /**
     * Un utente scrive un messaggio se rispecchia delle caratteristiche
     * La creazione dei messaggi avviene tramite l'utilizzo del pattern Strategy
     * @param request
     * @param httpServletRequest
     * @return
     */
    public String createCommentToMessage(CommentRequest request, HttpServletRequest httpServletRequest) {
        //Controllo ed estrapolazione utente dal Context di Security
        User user = Utils.getUserFromHeader(httpServletRequest, userRepository, jwtUtils);
        //Controllo che l'utente abbia effettuato una prenotazione dal rifornitore
        controlUserLocation(request.getUsername(), user);
        LocationMessage locationMessage = locationMessageRepository.findById(request.getIdMessage()).orElseThrow(()-> new IllegalStateException("Il messaggio indicato non esiste"));
        //Set della strategia di publicazione del commento
        context.setPublishingStrategy(new ConcreteStrategyLocation(locationRepository, locationMessageRepository, locationCommentRepository));
        //Eseguo la strategia di pubblicazione del messaggio per il rifornitore
        return context.executeCommentStrategy(request.getIdMessage(), user, request.getComment());
    }

    /**
     * Controlla che l'utente che vuole inserire un messaggio o un commento abbia almeno una volta effettuato l'acquisto di
     * un skipass dal rifornitore indicato
     * @param username
     * @param user
     */
    private void controlUserLocation(String username, User user) {
        //Controllo località, in questo caso getUsername mi da il nome della località
        Location location = getLocalitaByName(username);
        //Controllo che l'utente che scrive il messaggio abbia acquistato almeno una volta lo skipass per la località
        List<CardSkipass> cardsSkipass = location.getCardSkipasses();
        if(cardsSkipass.isEmpty()) throw new IllegalStateException("Non ci sono card per la località " + location.getName());
        List<BuySkipass> userBuySkipass = buySkipassRepository.findAllByUser(user);
        if(userBuySkipass.isEmpty()) throw new IllegalStateException("L'utente non ha ancora acquistato skipass per questa località" + location.getName());
    }

    /**
     * Data una location vengono ritornati tutti i messaggi che gli utenti hanno pubblicato e i rispettivi commenti
     * @param idLocation
     * @return
     */
    public MessageResponse getAllMessage(Integer idLocation) {
        Location location = locationRepository.findById(idLocation).orElseThrow(()-> new IllegalStateException("La località cercata non esiste"));
        List<LocationMessage> locationsMessage = locationMessageRepository.findAllByLocation(location);

        List<MessageDTO> messageDTOS = new ArrayList<>();

        //Crea una commentDTO nella quale ci sono i dati importanti di tutti i commenti legati ad un messaggio
        //Prende tutti i messaggi di una località (che vengono gestiti come DTO) e vengono aggiunti i relativi commenti per ogni messaggio
        locationsMessage.forEach(cur -> {
            List<CommentDTO> commentDTOS = new ArrayList<>();
            cur.getLocationComments().forEach(elem -> {
                commentDTOS.add(CommentDTO.builder().id(elem.getCommentId()).user(elem.getUser().getEmail()).comment(elem.getComment()).build());
            } );
            messageDTOS.add(MessageDTO.builder().idMessage(cur.getMessageId()).user(cur.getUser().getUserId()).message(cur.getMessage()).comments(commentDTOS).build());
        });
        return MessageResponse.builder().idLocation(idLocation).listMessage(messageDTOS).build();
    }

    /**
     *Il metodo elimina un messaggio e a cascata tutti i commenti che ad esso si riferiscono.
     * In questo caso si tratta di un messaggio creato da un utente sul forum di una località
     * Il compito è riservato all'admin del sistema
     * @param request
     * @return
     */
    public MessageDTO deleteMessage(MessageDTO request) {
        LocationMessage message = locationMessageRepository.findById(request.getIdMessage())
                .orElseThrow(()->new IllegalStateException("Il messaggio cercato non esiste"));
        locationMessageRepository.delete(message);
        return MessageDTO.builder()
                .idMessage(message.getMessageId())
                .message(message.getMessage())
                .user(message.getUser().getUserId())
                .build();
    }

    /**
     * Il metodo elimina una lista di commenti ritenuti in opportuni da parte dell'admin per un determinato messaggio
     * In questo caso si tratta di uno o più commenti che degli utenti fanno sul messaggio di un utente sul forum di una località
     * @param request
     * @return
     */
    public MessageDTO deleteComments(MessageDTO request) {
        if(request.getComments().isEmpty()) throw new IllegalStateException("Non ci sono commenti selezionati");
        LocationMessage message = locationMessageRepository.findById(request.getIdMessage())
                .orElseThrow(()-> new EntityNotFoundException("Il messaggio indicato non esiste"));
        List<LocationComment> comments = new ArrayList<>();
        request.getComments().forEach(cur -> comments.add(locationCommentRepository.findById(cur.getId())
                .orElseThrow(()-> new EntityNotFoundException("Un dei commenti indicati non esiste"))));
        locationCommentRepository.deleteAll(comments);
        return MessageDTO.builder()
                .idMessage(message.getMessageId())
                .comments(request.getComments())
                .build();
    }
}
