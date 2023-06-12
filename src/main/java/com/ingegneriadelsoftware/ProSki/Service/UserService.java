package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.BuySkipassRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.UserPlanRequest;
import com.ingegneriadelsoftware.ProSki.Email.CreatorEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Email.RegisterCreatorEmail;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.BuySkipassRepository;
import com.ingegneriadelsoftware.ProSki.Repository.LessonRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SkipassRepository;
import com.ingegneriadelsoftware.ProSki.Repository.UserRepository;
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
    private final JwtService jwtService;
    private final EmailSender emailSend;
    private final LessonRepository lessonRepository;
    private final PlanService planService;
    private final SkipassRepository skipassRepository;
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
        User userExists = (User) loadUserByUsername(user.getEmail());
        //Controllo che l'utente non sia ancora registrato
        if (userExists.isEnable()) throw new IllegalStateException("l'utente è già stato registrato correttamente");
        //Controllo che il token dato in fase di richiesta registrazione sia ancora valido
        try {
            jwtService.isTokenValid(userExists.getToken(), userExists);
        } catch (ExpiredJwtException e) {
            deleteUserByEmail(userExists.getEmail());
            throw new IllegalStateException("token precedente scaduto, registrare nuovamente l'utente");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //Genera il token per l'utente con un durata di 15 minuti
        String jwtToken = jwtService.generateToken(
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
        String emailUser = jwtService.findEmailUtenteByHttpServletRequest(servletRequest);
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
        String userEmail = jwtService.findEmailUtenteByHttpServletRequest(requestServlet);
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
     * tessera non sono stati effettuati acquisti futuri
     * @param request
     * @param httpRequest
     * @return
     */
    public BuySkipass buySkipassUser(BuySkipassRequest request, HttpServletRequest httpRequest) {
        //Prendo l'email dal token presente nella ServletRequest e da questo ricavo l'utente che sta facendo l'acquisto
        String userEmail = jwtService.findEmailUtenteByHttpServletRequest(httpRequest);
        User user = (User) loadUserByUsername(userEmail);
        //Controllo data acquisto
        LocalDate date = Utils.formatterData(request.getDate());
        if(date.isBefore(LocalDate.now())) throw new IllegalStateException("Data inserita errata");
        Integer discount = null;
        //Controllo esistenza tessera
        CardSkipass cardSkipass = skipassRepository.findByCardCode(request.getCardCode())
                .orElseThrow(()->new IllegalStateException("La card non esiste"));
        //Controllo che per la card non ci siano acquisti già effettuati dopo la data odierna
        List<BuySkipass> skipassBought = cardSkipass.getBuySkipasses();
        skipassBought.forEach(elem -> {
            if(!elem.getDate().isBefore(LocalDate.now()))
                throw new IllegalStateException("La tessera ha un abbonamento già caricato per il giorno " + elem.getDate());
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
        List<User> utenti = userRepository.findAllByDateBirthBetween(startDateBirth, endDateBirth);
        if(utenti.isEmpty()) throw new IllegalStateException("Non è stato trovato nessun utente con queste eta");
        return utenti;
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
