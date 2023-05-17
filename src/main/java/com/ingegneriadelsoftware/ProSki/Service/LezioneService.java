package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.LezioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LezioneResponse;
import com.ingegneriadelsoftware.ProSki.Email.CreatorEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Email.LezioneCreatorEmail;
import com.ingegneriadelsoftware.ProSki.Model.Lezione;
import com.ingegneriadelsoftware.ProSki.Model.Maestro;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import com.ingegneriadelsoftware.ProSki.Repository.LezioneRepository;
import com.ingegneriadelsoftware.ProSki.Repository.MaestroRepository;
import com.ingegneriadelsoftware.ProSki.Repository.UtenteRepository;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LezioneService {
    private final MaestroService maestroService;
    private final LezioneRepository lezioneRepository;
    private final EmailSender emailSend;
    private final UtenteRepository utenteRepository;
    private final MaestroRepository maestroRepository;

    @Transactional
    public Lezione createLezione(LezioneRequest request) {
        //Conversione ore da stringa a LocalDateTime
        LocalDateTime inizioLezione = Utils.formatterData(request.getInizioLezione());
        LocalDateTime fineLezione = Utils.formatterData(request.getFineLezione());

        Maestro maestro = maestroService.getMaestroByEmail(request.getEmailMaestro());
        //Verifica sulle date inserite
        if(!dataIsValid(maestro, inizioLezione,fineLezione)) throw new IllegalStateException("Maestro occupato in queste date ");

        //Per ogni utente correttamente registrato viene generata e inviata una mail costum che li avvisa della pubblicazione della lezione
        Iterable<Utente> utenti = utenteRepository.findAll();
        for (Utente utente : utenti) {
            if (utente.isEnable()) {
                CreatorEmail email = new LezioneCreatorEmail(maestro.getNome(), utente.getNome(), maestro.getSpecialita());
                emailSend.send(utente.getEmail(), email.render());
            }
        }
        //Creazione lezione
        Lezione lezione = new Lezione(
                maestro,
                inizioLezione,
                fineLezione
        );

        lezioneRepository.save(lezione);

        return lezione;
    }


    /**
     * Controllo nella data scelta per la lezione il maestro non abbia altre lezioni
     * @param maestro
     * @param inizio
     * @param fine
     * @return
     */
    private boolean dataIsValid(Maestro maestro, LocalDateTime inizio, LocalDateTime fine) {
        List<Lezione> list = lezioneRepository.findAllByMaestro(maestro);
        for(Lezione elem : list) {
            if (inizio.isAfter(elem.getInizioLezione()) && inizio.isBefore(elem.getFineLezione()) || (fine.isAfter(elem.getInizioLezione()) && fine.isBefore(elem.getFineLezione()) || inizio.isEqual(elem.getInizioLezione())
            ||(inizio.isBefore(elem.getFineLezione()) && fine.isBefore(elem.getFineLezione()))))
                return false;
        }
        return true;
    }

    /**
     * Cerca tutte le lezioni disponibili su tutto il territorio
     * @return
     */
    public List<LezioneResponse> getListLessons() throws EntityNotFoundException {
        Iterable<Lezione> lezioni = lezioneRepository.findAll();
        Optional<Maestro> maestro;
        List<LezioneResponse> listLezioni = new ArrayList<>();
        for(Lezione lezione: lezioni) {
            maestro = maestroRepository.findById(lezione.getMaestro().getId());
            listLezioni.add(LezioneResponse
                    .builder()
                    .idLezione(lezione.getId())
                    .maestro(maestro.get().getNome())
                    .inizioLezione(lezione.getInizioLezione().toString())
                    .fineLezione(lezione.getFineLezione().toString())
                    .build());
        }

        return listLezioni;
    }
}
