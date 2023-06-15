package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.CardSkipassRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LocationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.ConcreteStrategyLocation;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.Context;
import com.ingegneriadelsoftware.ProSki.Model.BuySkipass;
import com.ingegneriadelsoftware.ProSki.Model.CardSkipass;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Model.User;
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
    public String createLocation(LocationRequest request) throws DateTimeException {
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
        return "localita creata con successo";
    }

    /**
     * Creazione di skipass per una determinata località
     * @param request
     * @return
     */
    public String createSkipass(CardSkipassRequest request) throws EntityNotFoundException {
         Optional<CardSkipass> skipass = cardSkipassRepository.findByCardCode(request.getCardCode());
         if(skipass.isPresent()) throw new IllegalStateException("La tessera è già presente");
         Location location = getLocalitaByName(request.getLocation());
         CardSkipass newCardSkipass = new CardSkipass(request.getCardCode(), location);
         cardSkipassRepository.save(newCardSkipass);
         return "Lo Skipass è stato aggiunto correttamente alla località " + location.getName();
    }

    public String createMessage(MessageRequest request, HttpServletRequest httpServletRequest) {
        //Controllo esistenza utente
        User user = Utils.getUserFromHeader(httpServletRequest, userRepository, jwtUtils);
        //Controllo località, in questo caso getUsername mi da il nome della località
        Location location = getLocalitaByName(request.getUsername());
        //Controllo che l'utente che scrive il messaggio abbia acquistato almeno una volta lo skipass per la località
        List<CardSkipass> cardsSkipass = location.getCardSkipasses();
        if(cardsSkipass.isEmpty()) throw new IllegalStateException("Non ci sono card per la località " + location.getName());
        List<BuySkipass> userBuySkipass = buySkipassRepository.findAllByUser(user);
        if(userBuySkipass.isEmpty()) throw new IllegalStateException("L'utente non ha ancora acquistato skipass per questa località" + location.getName());

        context.setPublishingStrategy(new ConcreteStrategyLocation(locationRepository, locationMessageRepository));
        return context.executeStrategy(location.getName(), user, request.getMessage());

    }
}
