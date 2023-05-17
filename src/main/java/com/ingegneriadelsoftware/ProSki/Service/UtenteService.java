package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.IscrizioneLezioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LezioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LezioneResponse;
import com.ingegneriadelsoftware.ProSki.Email.*;
import com.ingegneriadelsoftware.ProSki.Model.Lezione;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import com.ingegneriadelsoftware.ProSki.Repository.LezioneRepository;
import com.ingegneriadelsoftware.ProSki.Repository.UtenteRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UtenteService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UtenteRepository utenteRepository;
    private final JwtService jwtService;
    private final EmailSender emailSend;
    private final LezioneRepository lezioneRepository;
    private final LezioneService lezioneService;

    @Override
    public UserDetails loadUserByUsername(String email) throws IllegalStateException {
        return utenteRepository.findUserByEmail(email).
                orElseThrow(()-> new IllegalStateException("l'utente non è stato trovato"));
    }

    /**
     * Controllo se l'utente è già registrato.
     * Per ogni utente che si può registrare viene generato un token che ha validità 15 minuti.
     * Invio della mail per la registrazione all'indirizzo di posta elettronica.
     * @param utente
     * @return String
     * @throws IllegalStateException
     */
    public String iscrizione(Utente utente) throws IllegalStateException {
        Optional<Utente> utenteEsiste = utenteRepository.findUserByEmail(utente.getEmail());

        if(utenteEsiste.isPresent()) {
            if (utenteEsiste.get().isEnable())
                throw new IllegalStateException("l'utente esiste");
            try {
                jwtService.isTokenValid(utenteEsiste.get().getToken(), utenteEsiste.get());
            } catch (ExpiredJwtException e) {
                deleteUtenteByEmail(utenteEsiste.get().getEmail());
                throw new IllegalStateException("token precedente scaduto, registrare nuovamente l'utente");
            }
            throw new IllegalStateException("l'utente esiste");
        }

        utente.setPassword(passwordEncoder.encode(utente.getPassword()));

        String jwtToken = jwtService.generateToken(
                utente,
                new Date(System.currentTimeMillis() + 15 * 1000 * 60)
        );

        utente.setToken(jwtToken);

        String link = "http://localhost:8080/api/v1/profilo/confirm?token=" + jwtToken;
        CreatorEmail email = new RegistrazioneCreatorEmail(utente.getNome(), link);
        emailSend.send(utente.getEmail(), email.render());

        utenteRepository.save(utente);
        return jwtToken;
    }

    public void abilitaUtente(String email) {
        utenteRepository.enableUtente(email);
    }

    public void deleteUtenteByEmail(String email) {
        utenteRepository.deleteByEmail(email);
    }

    /**
     * Il metodo viene utilizzato per la registrazione quindi prende in considerazione il token nella classe Utente del DB
     * e non dal contesto di Spring Security
     * @param token
     * @return
     */
    public Utente findUtenteByToken(String token) {
        Optional<Utente> utente = utenteRepository.findByToken(token);
        if(utente.isPresent())
            return utente.get();
        return null;
    }


    /**
     * trova tutte le lezioni di un utente
     * @param servletRequest
     * @return
     */
    public List<Lezione> getLezioniByUtente(HttpServletRequest servletRequest) throws EntityNotFoundException {
        //Prende l'email dell'utente dal SecurityContext e ricava l'utente dal DB
        String emailUtente = jwtService.findEmailUtenteBySecurityContext(servletRequest);
        Utente utente = (Utente) loadUserByUsername(emailUtente);

        return utente.getLezioniUtente();
    }

    public String iscrizioneLezioni(List<IscrizioneLezioneRequest> request, HttpServletRequest requestServlet) throws EntityNotFoundException {
        //Ricavo l'utente dal suo token
        String userEmail = jwtService.findEmailUtenteBySecurityContext(requestServlet);
        Utente utente = (Utente) loadUserByUsername(userEmail);
        List<Lezione> list = new ArrayList<>();

        request.forEach(cur-> {
            list.add(getLezioneById(cur.getId()));
        });

        utente.setLezioniUtente(list);

        utenteRepository.save(utente);
        return "Iscrizione avvenuta con successo";
    }

    public Lezione getLezioneById(Integer id) throws EntityNotFoundException {
        return lezioneRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("La lezione cercata non esiste"));
    }
}
