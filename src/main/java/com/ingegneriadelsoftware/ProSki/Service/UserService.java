package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.BuySkipassRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.UserPlanRequest;
import com.ingegneriadelsoftware.ProSki.EmailFactory.CreatorEmail;
import com.ingegneriadelsoftware.ProSki.EmailFactory.EmailSender;
import com.ingegneriadelsoftware.ProSki.EmailFactory.RegisterCreatorEmail;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.BuySkipassRepository;
import com.ingegneriadelsoftware.ProSki.Repository.LessonRepository;
import com.ingegneriadelsoftware.ProSki.Repository.CardSkipassRepository;
import com.ingegneriadelsoftware.ProSki.Repository.UserRepository;
import com.ingegneriadelsoftware.ProSki.Security.JwtUtils;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final EmailSender emailSend;
    private final LessonRepository lessonRepository;
    private final PlanService planService;
    private final CardSkipassRepository cardSkipassRepository;
    private final OfferService offerService;
    private final BuySkipassRepository buySkipassRepository;
    private final LessonService lessonService;

    @Override
    public UserDetails loadUserByUsername(String email) throws IllegalStateException {
        return userRepository.findUserByEmail(email).orElseThrow(()-> new IllegalStateException("l'utente non è stato trovato"));
    }


    /**
     * Controllo se l'utente è già registrato.
     * Per ogni utente che si può registrare viene generato un token che ha validità 15 minuti.
     * Invio della mail per la registrazione all'indirizzo di posta elettronica.
     * @param user
     * @return String
     * @throws IllegalStateException
     */
    public String registration(User user) throws IllegalStateException {
        //Controllo che l'utente sia correttamente registrato: esiste nella base di dati e il suo stato uguale a enable
        Optional<User> utenteEsiste = userRepository.findUserByEmail(user.getEmail());
        if(utenteEsiste.isPresent()) {
            if (utenteEsiste.get().isEnable())
                throw new IllegalStateException("l'utente esiste");
            try {
                jwtUtils.isTokenValid(utenteEsiste.get().getToken(), utenteEsiste.get());
            } catch (ExpiredJwtException e) {
                deleteUserByEmail(utenteEsiste.get().getEmail());
                throw new IllegalStateException("token precedente scaduto, registrare nuovamente l'utente");
            }
            throw new IllegalStateException("l'utente esiste");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //Genera il token per l'utente con un durata di 15 minuti
        String jwtToken = jwtUtils.generateToken(
                user,
                new Date(System.currentTimeMillis() + 15 * 1000 * 60)
        );
        user.setToken(jwtToken);
        //Invio della mail
        String link = "http://localhost:8080/api/v1/profilo/confirm?token=" + jwtToken;
        //Creazione della mail costum per la registrazione
        CreatorEmail email = new RegisterCreatorEmail(user.getName(), link);
        emailSend.send(user.getEmail(), email.render());

        userRepository.save(user);
        return jwtToken;
    }

    /**
     * trova tutte le lezioni di un utente
     * @param servletRequest
     * @return
     */
    public List<Lesson> getLezioniByUtente(HttpServletRequest servletRequest) throws EntityNotFoundException {
        //Prendo l'email dal token presente nella ServletRequest e da questo ricavo l'utente che sta effettuando la prenotazione
        String emailUser = jwtUtils.findEmailUtenteByHttpServletRequest(servletRequest);
        User user = (User) loadUserByUsername(emailUser);
        return user.getUsersLessons();
    }

    /**
     * Iscrive un utente ad una lezione, identificata da un Id
     * @param lessonId
     * @param requestServlet
     * @return
     * @throws EntityNotFoundException
     */
    public String registrationLesson(Integer lessonId, HttpServletRequest requestServlet) throws EntityNotFoundException {
        //Prendo l'email dal token presente nella ServletRequest e da questo ricavo l'utente che sta effettuando la prenotazione
        String userEmail = jwtUtils.findEmailUtenteByHttpServletRequest(requestServlet);
        User user = (User) loadUserByUsername(userEmail);
        //Controllo se la lezione esiste
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(()-> new EntityNotFoundException("Lezione non trovata"));
        List<Lesson> userLessons = user.getUsersLessons();
        //Controllo che l'utente non si iscriva a due lezioni contemporaneamente
        if(!lessonService.dataIsValid(userLessons, lesson.getStartLesson(), lesson.getEndLesson()))
            throw new IllegalStateException("L'utente non può iscriversi contemporaneamente a due lezioni");
        userLessons.add(lesson);
        userRepository.save(user);
        return "Iscrizione avvenuta con successo";
    }

    public String insertUserPlan(UserPlanRequest request) {
        //Prendo l'email dal token presente nella ServletRequest e da questo ricavo l'utente da inserire al piano
        User user = (User) loadUserByUsername(request.getEmail());
        if(!user.isEnable()) throw new IllegalStateException("L'utente cercato non ha ancora effettuato la registrazione");
        Plan plan = planService.getPlanByName(request.getPlan());
        if(user.getPlan() != null) throw new IllegalStateException("Un utente può avere un solo piano associato alla volta");
        //Inserisco utente al piano
        user.setPlan(plan);
        userRepository.save(user);
        return "Il piano " + plan.getName() + " è stato aggiunto correttamente all'utente " + user.getName();
    }

    /**
     * Dato un utente e il codice della tessera, il metodo controlla se è presente un'offerta per la data selezionata e
     * applica lo sconto al prezzo dell'abbonamento, infine avviene l'acquisto. Si può acquistare uno skipass solo se con la stessa
     * tessera non sono stati effettuati acquisti futuri e un utente può acquistare uno skipass al giorno
     * @param request
     * @param httpRequest
     * @return
     */
    public BuySkipass buySkipassUser(BuySkipassRequest request, HttpServletRequest httpRequest) {
        //Prendo l'email dal token presente nella ServletRequest e da questo ricavo l'utente che sta facendo l'acquisto
        String userEmail = jwtUtils.findEmailUtenteByHttpServletRequest(httpRequest);
        User user = (User) loadUserByUsername(userEmail);
        //Controllo data acquisto
        LocalDate date = Utils.formatterData(request.getDate());
        if(date.isBefore(LocalDate.now())) throw new IllegalStateException("Data inserita errata");
        Integer discount = 0;
        //Controllo esistenza tessera
        CardSkipass cardSkipass = cardSkipassRepository.findByCardCode(request.getCardCode())
                .orElseThrow(()->new IllegalStateException("La card non esiste"));
        //Controllo che per la card non ci siano acquisti già effettuati dopo la data odierna
        List<BuySkipass> skipassBought = cardSkipass.getBuySkipasses();
        skipassBought.forEach(elem -> {
            if(!elem.getDate().isBefore(LocalDate.now()))
                throw new IllegalStateException("La tessera ha un abbonamento già caricato per il giorno " + elem.getDate());
        });
        //Controllo che l'utente abbia acquistato un solo skipass per una giornata
        user.getBuySkipasses().forEach(cur->{
            if(cur.getDate().isEqual(date))
                throw new IllegalStateException("L'utente ha già acquistato uno skipass per il giorno " + cur.getDate());
        });

        //Controllo esistenza piano per utente e offerta
        if(user.getPlan() != null)
            discount = offerService.getSconto(user.getPlan().getOffer(), date);
        Double priceSubscription = cardSkipass.getLocation().getPriceSubscription();
        Double actualPrice = priceSubscription - priceSubscription * discount / 100;
        //Acquisto skipass
        BuySkipass buySkipass = new BuySkipass(user, cardSkipass, actualPrice, date);
        buySkipassRepository.save(buySkipass);
        return buySkipass;
    }

    /**
     * Controllo il valore delle date inserite e ritorno una lista di utenti che hanno eta compresa tra StartEta e EndEta
     * @param startAge
     * @param endAge
     * @return
     */
    public List<User> getUsersByAgeBetween(Integer startAge, Integer endAge) {
        if(!(startAge > 0 && endAge > 0) || startAge > endAge)
            throw  new IllegalStateException("Le date inserite non sono valide");
        //Conversione Data da Integer a LocalDate, considerando che gli utenti più grandi hanno una data di nascita meno recente
        LocalDate endDateBirth = LocalDate.now().minusYears(startAge);
        LocalDate startDateBirth = LocalDate.now().minusYears(endAge);
        List<User> users = userRepository.findAllByDateBirthBetween(startDateBirth, endDateBirth);
        //Filtra per soli utenti registrati
        List<User> usersRegister = users.stream().filter(User::isEnable).toList();
        if(usersRegister.isEmpty()) throw new IllegalStateException("Non è stato trovato nessun utente con queste eta");
        return usersRegister;
    }

    public List<User> getAllUsersByGender(String gender) {
        if(!gender.equalsIgnoreCase("man") && !gender.equalsIgnoreCase("woman"))
            throw new IllegalStateException("Sono permessi valori come man o woman");
        return userRepository.findAllByGender(Gender.valueOf(gender.toUpperCase()));
    }

    public String deleteUserByEmail(String email) {
        if(email.isEmpty()) throw new IllegalStateException("Email non presente");
        User user = (User) loadUserByUsername(email);
        userRepository.delete(user);
        return "L'utente " +user.getEmail() + " è stato eliminato correttamente";
    }
}