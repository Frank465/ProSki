package com.ingegneriadelsoftware.ProSki.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingegneriadelsoftware.ProSki.Controller.LessonController;
import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LessonRequest;
import com.ingegneriadelsoftware.ProSki.Mapping.Request;
import com.ingegneriadelsoftware.ProSki.Model.Instructor;
import com.ingegneriadelsoftware.ProSki.Model.Lesson;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Service.InstructorService;
import com.ingegneriadelsoftware.ProSki.Service.LessonService;
import com.ingegneriadelsoftware.ProSki.Stub;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;

import java.time.DateTimeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LessonControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private LessonService lessonService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createLesson(@Valid @RequestBody LessonRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toLessonResponseByLesson(lessonService.createLesson(request)));
        } catch (IllegalStateException | EntityNotFoundException | DateTimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Questo test verifica che l'endpoint per la creazione di una lezione restituisca lo stato "OK" (200)
     * quando viene chiamato con un utente autenticato come amministratore.
     * Viene simulata una richiesta POST all'endpoint "/api/v1/lesson/create" con un corpo di richiesta JSON
     * che rappresenta la lezione da creare. L'implementazione del servizio delle lezioni (lessonService)
     * viene configurata per restituire la lezione di esempio specificato quando viene chiamato con qualsiasi argomento.
     * Se l'endpoint restituisce lo stato "OK" (200), il test viene superato.
     * A fare la chiamata è un ADMIN come correttamente configurato nel MockUser
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void LessonController_CreateLesson_ReturnOk() throws Exception {
        Lesson lesson = Stub.getLessonsStub();
        LessonRequest lessonRequest = Request.toLessonRequestByLessonMapper(lesson);
        String lessonRequestAsString = new ObjectMapper().writeValueAsString(lessonRequest);
        Mockito.when(lessonService.createLesson(any())).thenReturn(lesson);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lesson/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(lessonRequestAsString))
                .andExpect(status().isOk());
    }

    /**
     * Questo test verifica che l'endpoint per la creazione di una lezione restituisca lo stato (400 BadRequest)
     * quando viene chiamato con un utente autenticato come amministratore.
     * Viene simulata una richiesta POST all'endpoint "/api/v1/lesson/create" con un corpo di richiesta JSON
     * che rappresenta la lezione da creare che però ha l'email del maestro in un formato sbagliato.
     * L'implementazione del servizio delle lezioni (lessonService)
     * viene configurata per restituire la lezione di esempio specificato quando viene chiamato con qualsiasi argomento.
     * Se l'endpoint restituisce lo stato "OK" (400), il test viene superato.
     * A fare la chiamata è un ADMIN come correttamente configurato nel MockUser
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void LessonController_CreateLesson_ReturnBadRequest() throws Exception {
        Lesson lesson = Stub.getLessonsStub();
        LessonRequest lessonRequest = Request.toLessonRequestByLessonMapper(lesson);
        lessonRequest.setInstructorEmail("Email falsa");
        String lessonRequestAsString = new ObjectMapper().writeValueAsString(lessonRequest);
        Mockito.when(lessonService.createLesson(any())).thenReturn(lesson);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lesson/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(lessonRequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Questo test verifica che l'endpoint per la creazione di una lezione restituisca lo stato (403 Forbidden)
     * quando viene chiamato con un utente autenticato come user.
     * Viene simulata una richiesta POST all'endpoint "/api/v1/lesson/create" con un corpo di richiesta JSON
     * che rappresenta la lezione da creare. L'implementazione del servizio delle lezioni (lessonService)
     * viene configurata per restituire la lezione di esempio specificato quando viene chiamato con qualsiasi argomento.
     * Se l'endpoint restituisce lo stato "Forbidden" (403), il test viene superato.
     * A fare la chiamata è uno user siccome è l'uteten di defaul del MmockUser se non configurato
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LessonController_CreateLesson_ReturnForbidden() throws Exception {
        Lesson lesson = Stub.getLessonsStub();
        LessonRequest lessonRequest = Request.toLessonRequestByLessonMapper(lesson);
        String lessonRequestAsString = new ObjectMapper().writeValueAsString(lessonRequest);
        Mockito.when(lessonService.createLesson(any())).thenReturn(lesson);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lesson/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(lessonRequestAsString))
                .andExpect(status().isForbidden());
    }

    /**
     * Questo test verifica che l'endpoint per ottenere tutte le lezioni ritorni uno stato (200Ok)
     * quando viene chiamato con un utente autenticato.
     * Viene simulata una richiesta GET all'endpoint "/api/v1/lesson/getAll".
     * L'implementazione del servizio degli appuntamenti (lessonService) viene configurata per ritornare
     * la lista di tutte le lezioni disponibili
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LessonController_getAllLessons_ReturnOk() throws Exception {
        Mockito.when(lessonService.getListLessons()).thenReturn(Stub.getLessonsDTOStub());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lesson/getAll"))
                .andExpect(status().isOk());
    }

    /**
     * Questo test verifica che l'endpoint per ottenere tutte le lezioni ritorni uno stato "Internal Server Error" (500)
     * quando viene chiamato con un utente autenticato.
     * Viene simulata una richiesta GET all'endpoint "/api/v1/lesson/getAll".
     * L'implementazione del servizio degli appuntamenti (lessonService) viene configurata per lanciare
     * un'eccezione di tipo EntityNotFoundException.
     * Se l'endpoint restituisce lo stato "Internal Server Error" (500), il test viene superato.
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LessonController_GetAllLessons_ReturnInternalServerError() throws Exception {
        Mockito.when(lessonService.getListLessons()).thenThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lesson/getAll"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Questo test verifica che l'endpoint per ottenere le lezioni di un maestro che restituisca lo stato "OK" (200)
     * Viene simulata una richiesta GET all'endpoint "/api/v1/lesson/getAll/byInstructor/{instructorId}" con l'ID dell'istruttore specificato nell'URL.
     * Viene configurata per restituire una lista di appuntamenti di esempio tramite il metodo getLessonsDTOStub()
     * Se l'endpoint restituisce lo stato "OK" (200), il test viene superato.
     * tramite @WithMockUser, che fornisce un utente di default per il test.
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LessonController_GetLessonsByInstructor_ReturnOk() throws Exception {
        Instructor instructor = Stub.getInstructorStub();
        Mockito.when(lessonService.getListLessonsByInstructor(any())).thenReturn(Stub.getLessonsDTOStub());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lesson/getAll/byInstructor/" + instructor.getId()))
                .andExpect(status().isOk());
    }

    /**
     * Questo test verifica che l'endpoint per ottenere le lezioni di un maestro che restituisca lo stato
     * (500 Internal Server Error)
     * Viene simulata una richiesta GET all'endpoint "/api/v1/lesson/getAll/byInstructor/{instructorId}"
     * con l'ID dell'istruttore specificato nell'URL.
     * il metodo getListLessonsByInstructor() viene configurato per sollevare un EntityNotFoundException
     * Se l'endpoint restituisce lo stato (500 Internal Server Error), il test viene superato.
     * tramite @WithMockUser, che fornisce un utente di default per il test.
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LessonController_GetLessonsByInstructor_ReturnInternalServerError() throws Exception {
        Instructor instructor = Stub.getInstructorStub();
        Mockito.when(lessonService.getListLessonsByInstructor(any())).thenThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lesson/getAll/byInstructor/" + instructor.getId()))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Questo test verifica che l'endpoint per ottenere le lezioni di una località che restituisca lo stato "OK" (200)
     * Viene simulata una richiesta GET all'endpoint "/api/v1/lesson/getAll/byInstructor/{instructorId}"
     * con l'ID della località specificato nell'URL.
     * la chiamata al metodo getListLessonsByLocation() viene configurata per restituire una lista di
     * lezioni di esempio tramite il metodo getLessonsDTOStub()
     * Se l'endpoint restituisce lo stato "OK" (200), il test viene superato.
     * tramite @WithMockUser, che fornisce un utente di default per il test.
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LessonController_GetLessonsByLocation_ReturnOk() throws Exception {
        Location location = Stub.getLocationStub();
        Mockito.when(lessonService.getListLessonsByLocation(any())).thenReturn(Stub.getLessonsDTOStub());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lesson/getAll/byLocation/" + location.getLocationId()))
                .andExpect(status().isOk());
    }

    /**
     * Questo test verifica che l'endpoint per ottenere le lezioni di un maestro che restituisca lo stato
     * (500 Internal Server Error)
     * la chiamata al metodo getListLessonsByLocation() viene simulata una richiesta
     * GET all'endpoint "/api/v1/lesson/getAll/byInstructor/{instructorId}"
     * con l'ID dell'istruttore specificato nell'URL.
     * il metodo getListLessonsByInstructor() viene configurato per sollevare un EntityNotFoundException
     * Se l'endpoint restituisce lo stato (500 Internal Server Error), il test viene superato.
     * tramite @WithMockUser, che fornisce un utente di default per il test.
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LessonController_GetLessonsByLocation_ReturnInternalServerError() throws Exception {
        Location location = Stub.getLocationStub();
        Mockito.when(lessonService.getListLessonsByLocation(any())).thenThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lesson/getAll/byLocation/" + location.getLocationId()))
                .andExpect(status().isInternalServerError());
    }
}
