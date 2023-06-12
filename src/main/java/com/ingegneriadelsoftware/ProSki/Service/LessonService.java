package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.LessonRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LessonResponse;
import com.ingegneriadelsoftware.ProSki.Email.CreatorEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Email.LessonCreatorEmail;
import com.ingegneriadelsoftware.ProSki.Model.Lesson;
import com.ingegneriadelsoftware.ProSki.Model.Instructor;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Repository.LessonRepository;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorRepository;
import com.ingegneriadelsoftware.ProSki.Repository.UserRepository;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final InstructorService instructorService;
    private final LessonRepository lessonRepository;
    private final EmailSender emailSend;
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;
    private final LocationService locationService;

    /**
     * Viene creata una lezione a partire dalla request(DTO), le date da stringhe vengono convertite. Viene preso l'istruttore
     * a cui si fa riferimento e tutte le sue lezioni, quindi si vede se la nuova lezione è compatibile con gli orari delle altre
     * leioni e con gli orari dell'apertura degli impianti della località.
     * Ogni utente registrato al sito viene notificato attraverso una mail che è stata create una nuova lezione
     * @param request
     * @return
     * @throws EntityNotFoundException
     * @throws DateTimeException
     */
    @Transactional
    public Lesson createLesson(LessonRequest request) throws EntityNotFoundException, DateTimeException {
        //Conversione ore da stringa a LocalDateTime
        LocalDateTime startLesson = Utils.formatterDataTime(request.getStartLesson());
        LocalDateTime endLesson = Utils.formatterDataTime(request.getEndLesson());
        if(!startLesson.isAfter(LocalDateTime.now()) || endLesson.isBefore(startLesson))
            throw new IllegalStateException("Le data di inizio deve essere nel futuro e non può precedere la data di fine");
        //Verifica data lezione con le altre lezioni del maestro
        Instructor instructor = instructorService.getInstructorByEmail(request.getInstructorEmail());
        List<Lesson> list = instructor.getLessonList();
        //Controllo che date inserite rientrano nei vincoli di date della localita su stagione e ora apertura impianti
        Location location = instructor.getLocation();
        if(startLesson.toLocalDate().isBefore(location.getStartOfSeason())
                || endLesson.toLocalDate().isAfter(location.getEndOfSeason())
                || startLesson.toLocalTime().isBefore(location.getOpeningSkiLift())
                || endLesson.toLocalTime().isAfter(location.getClosingSkiLift()))
            throw new IllegalStateException("La data della lezione deve essere compresa tra " + location.getStartOfSeason()
                    + " e " + location.getEndOfSeason()+ " mentre, l'orario della lezione deve essere compreso tra "
                    + " " + location.getOpeningSkiLift() + " e " + location.getClosingSkiLift());
        //Controllo che il maestro non abbia già lezioni nelle date presenti nella request
        if(!dataIsValid(list, startLesson,endLesson)) throw new IllegalStateException("Maestro occupato in queste date ");
        //Per ogni utente correttamente registrato viene generata e inviata una mail costum che li avvisa
        //della pubblicazione della lezione
        Iterable<User> utenti = userRepository.findAll();
        for (User user : utenti) {
            if (user.isEnable()) {
                CreatorEmail email = new LessonCreatorEmail(instructor.getName(), user.getName(), instructor.getSpeciality());
                emailSend.send(user.getEmail(), email.render());
            }
        }
        //Creazione lezione
        Lesson lesson = new Lesson(instructor, startLesson, endLesson);
        lessonRepository.save(lesson);
        return lesson;
    }

    /**
     * Controllo se le date di inizio e di fine non si sovrappongono alle date presenti nella lista
     * @param list
     * @param inizio
     * @param fine
     * @return
     */
    public boolean dataIsValid(List<Lesson> list, LocalDateTime inizio, LocalDateTime fine) {
        for(Lesson elem : list) {
            if ((inizio.isAfter(elem.getStartLesson()) && inizio.isBefore(elem.getEndLesson()))
                    ||(fine.isAfter(elem.getStartLesson()) && fine.isBefore(elem.getEndLesson()))
                    ||(inizio.isEqual(elem.getStartLesson())))
                return false;
        }
        return true;
    }

    /**
     * Cerca tutte le lezioni disponibili su tutto il territorio
     * @return
     */
    public List<LessonResponse> getListLessons() throws EntityNotFoundException {
        Iterable<Lesson> lessons = lessonRepository.findAll();
        Instructor instructor;
        List<LessonResponse> lessonsList = new ArrayList<>();
        for(Lesson lesson : lessons) {
            instructor = instructorRepository.findById(lesson.getInstructor().getId())
                    .orElseThrow(()-> new EntityNotFoundException("Maestro "+ lesson.getInstructor().getId()+" non presente"));
            lessonsList.add(LessonResponse
                    .builder()
                    .idLesson(lesson.getId())
                    .instructor(instructor.getEmail())
                    .startLesson(lesson.getStartLesson().toString())
                    .endLesson(lesson.getEndLesson().toString())
                    .build());
        }
        return lessonsList;
    }
}
