package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.LessonRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LessonResponse;
import com.ingegneriadelsoftware.ProSki.Email.FactoryMethod.CreatorEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Email.FactoryMethod.LessonCreatorEmail;
import com.ingegneriadelsoftware.ProSki.Model.Lesson;
import com.ingegneriadelsoftware.ProSki.Model.Instructor;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Repository.LessonRepository;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorRepository;
import com.ingegneriadelsoftware.ProSki.Repository.LocationRepository;
import com.ingegneriadelsoftware.ProSki.Repository.UserRepository;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final EmailSender emailSend;
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;
    private final LocationRepository locationRepository;


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
    public Lesson createLesson(LessonRequest request) throws EntityNotFoundException, DateTimeException, MessagingException {
        //Conversione ore da stringa a LocalDateTime
        LocalDateTime startLesson = Utils.formatterDataTime(request.getStartLesson());
        LocalDateTime endLesson = Utils.formatterDataTime(request.getEndLesson());
        if(!startLesson.isAfter(LocalDateTime.now()) || !endLesson.isAfter(startLesson))
            throw new IllegalStateException("Le data di inizio deve essere nel futuro e non può precedere la data di fine o essere uguale");
        //Verifica data lezione con le altre lezioni del maestro
        Instructor instructor = instructorRepository.findByEmail(request.getInstructorEmail())
                .orElseThrow(()->new EntityNotFoundException("Il maestro non esiste"));
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
            if (inizio.isEqual(elem.getStartLesson())
                    || !(inizio.isBefore(elem.getStartLesson()) && fine.isBefore(elem.getStartLesson())
                    || inizio.isAfter(elem.getEndLesson())))
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

    /**
     * Il metodo ritorna tutte le lezioni di un maestro che ancora si devono svolgere.
     * Attraverso una LessonResponse si viene a conoscenza dell'Id della lezione del maestro e dell'orario di inizio
     * e fine lezione.
     * @param idInstructor
     * @return
     */
    public List<LessonResponse> getListLessonsByInstructor(Integer idInstructor) {
        Instructor instructor = instructorRepository.findById(idInstructor).orElseThrow(()->new EntityNotFoundException("maestro non trovato"));
        List<Lesson> lessonsInstructor = lessonRepository.findAllByInstructor(instructor);
        //Seleziono tutte le lezioni che hanno una data successiva a quella odierna
        List <Lesson> lessonAfterNow = lessonsInstructor.stream().filter(cur -> cur.getStartLesson().isAfter(LocalDateTime.now())).toList();
        List<LessonResponse> lessonsResponse = new ArrayList<>();
        lessonAfterNow.forEach( elem -> {
            lessonsResponse.add(LessonResponse.builder()
                    .idLesson(elem.getId())
                    .instructor(elem.getInstructor().getEmail())
                    .startLesson(elem.getStartLesson().toString())
                    .endLesson(elem.getEndLesson().toString())
                    .build());
        });
        return lessonsResponse;
    }

    /**
     * Il metodo ritorna tutte le lezioni che avvengo in una località antecedenti al momento stesso.
     * Viene ritornata una lista di LessonResponse in cui si evince idLesson, Maestro, inizio e fine lezione
     * @param idLocation
     * @return
     */
    public List<LessonResponse> getListLessonsByLocation(Integer idLocation) {
        Location location = locationRepository.findById(idLocation).orElseThrow(()-> new EntityNotFoundException("La localià non esiste"));
        List<Instructor> instructorsLocation = location.getInstructors();
        List<LessonResponse> lessonsResponse = new ArrayList<>();
        //Per ogni istruttore della località prendo le sue lezioni
        instructorsLocation.forEach(elem->{
            //Filtro le lezioni considerando solo quelle non ancora avvenute
            List<Lesson> lessonsAfterNow = elem.getLessonList().stream().filter(cur -> cur.getStartLesson().isAfter(LocalDateTime.now())).toList();
            //Per ogni lezione da farsi creo il DTO per la response
            lessonsAfterNow.forEach(cur -> {
                lessonsResponse.add(LessonResponse.builder()
                                .idLesson(cur.getId())
                                .instructor(cur.getInstructor().getEmail())
                                .startLesson(cur.getStartLesson().toString())
                                .endLesson(cur.getEndLesson().toString())
                        .build());
            });
        });
        return lessonsResponse;
    }
}
