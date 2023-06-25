package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.RegisterRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.AuthenticationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.RegisterResponse;
import com.ingegneriadelsoftware.ProSki.Model.Role;
import com.ingegneriadelsoftware.ProSki.Model.Gender;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Repository.UserRepository;
import com.ingegneriadelsoftware.ProSki.Security.JwtUtils;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;


/**
 * Service che si occupa della registrazione, conferma e login di un utente
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    /**
     * attributi che vengono iniettati nella classe per utilizzare i vari services
     */
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra utente con email come identificativo e controlla l'eventuale presenza
     * @param request
     * @return RegistrazioneResponse contiene un attributo token di tipo String
     */
    public RegisterResponse register(RegisterRequest request) throws IllegalStateException{
        String jwtToken;
        Gender gender;
        //Controllo data inserita
        LocalDate dateBirth = Utils.formatterData(request.getDateBirth());
        if(Period.between(dateBirth, LocalDate.now()).getYears() < 18 || dateBirth.isAfter(LocalDate.now()))
            throw new IllegalStateException("Errore data di nascita");
        //Controllo sesso inserito, il primo controllo si fa nel DTO per quanto riguarda la stringa, qui si associa solo
        if(request.getGender().equalsIgnoreCase("man"))
            gender = Gender.MAN;
        else
            gender = Gender.WOMAN;

        jwtToken = userService.registration(
                new User(
                    request.getName(),
                    request.getSurname(),
                    request.getPassword(),
                    gender,
                    dateBirth,
                    request.getEmail(),
                    Role.USER)
        );
        return RegisterResponse.builder().token(jwtToken).build();
    }

    /**
     * Conferma la registrazione di un utente dopo che ha verificato tramite email, se il token è scaduto l'utente viene eliminato
     * @return String
     */
    public String confirmToken(String token) throws IllegalStateException {
        User user = userRepository.findByToken(token).orElseThrow(()->new IllegalStateException("Utente non esiste"));
        if(user.isEnable()) throw new IllegalStateException("l'utente è gia registrato");
        try{
            jwtUtils.isTokenValid(token, user);
        }catch(ExpiredJwtException e) {
            userService.deleteUserByEmail(user.getEmail());
            throw new IllegalStateException("Token scaduto, registrazione fallita");
        }
        //L'utente viene registrato al sito
        userRepository.enableUser(user.getEmail());
        return "Confirm";
    }

    /**
     * Login di un utente attraverso jwt.
     * Vengono passate le credenziali da parte di un utente, queste attraverso authenticationManager di Spring Security
     * vengono controllare verificando che l'utente sia registrato correttamente.
     * Poi viene generato il token passandogli l'utente trovato nel ContextSecurity e la durata del token (1 giorno)
     * Infine viene create la risposta che è formata dal token(stringa)
     * @param request
     */
    public AuthenticationResponse authentication(AuthenticationRequest request) {
        UserDetails user;
        Authentication auth;
        //Controlla se l'utente con queste credenziali esiste nel SecurityContext
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        }catch(AuthenticationException e) {
            throw new IllegalStateException(e.getMessage());
        }
        //Prende le credenziali dell'utente che sta facendo l'autenticazione
        user = (UserDetails) auth.getPrincipal();

        //Genera il token associato all'utente con una durata di 24 ore
        String jwtToken = jwtUtils.generateToken(
                user,
                new Date(System.currentTimeMillis() + 1000 * 3600 * 24)
        );

        //Se è l'admin ad aver eseguito il login faccio il setup del Data Base
        if(user.getAuthorities().toString().contains("ADMIN"))
            setupUsers();

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    /**
     * Il metodo elimina tutti gli utenti che non hanno confermato la registrazione e che il token
     * fornito nel momento della registrazione (durata 15 minuti) sia scaduto
     */
    public void setupUsers() {
        List<User> userList = userRepository.findAllByEnable(false);
        userList.forEach( cur -> {
            try {
                jwtUtils.isTokenExpired(cur.getToken());
            }catch (ExpiredJwtException e) {
                userRepository.delete(cur);
            }
        });
    }
}
