package com.ingegneriadelsoftware.ProSki.ControllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.UserPlanRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LessonResponse;
import com.ingegneriadelsoftware.ProSki.Mapping.Request;
import com.ingegneriadelsoftware.ProSki.Model.Lesson;
import com.ingegneriadelsoftware.ProSki.Model.Plan;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Service.UserService;
import com.ingegneriadelsoftware.ProSki.Stub;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * Questo metodo testa la chiamata al endpoint getAllLessons di un utente, utilizzando JUnit e Mockito..
     * Inizialmente viene chiamato un metodo per il mock delle lezioni di un utente così da testare il metodo chiamato
     * dal service userService.getAllLessons. Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/getAll/lessons", in questo caso il context di Spring Security inietta la HttpServletRequest che contiene
     * le informazioni dell'utente.Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 OK),
     * mentre vengono anche controllati i valori di risposta siano corretti. La risposta del metodo è di tipo List<LessonResponse>
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenLessonsUser_getAll_ReturnOk() throws Exception {
        List<Lesson> lessons = Stub.getAllLessonsStub();
        given(userService.getLessonsByUser(any())).willReturn(lessons);
        List<LessonResponse> lessonResponseList = new ArrayList<>();
        lessons.forEach(elem -> {lessonResponseList.add(DTOManager.toLessonResponseByLesson(elem));});
        System.out.println(lessonResponseList);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAll/lessons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idLesson", is(1)))
                .andExpect(jsonPath("$[0].instructor", is("maestro1")))
                .andExpect(jsonPath("$[1].idLesson", is(2)))
                .andExpect(jsonPath("$[1].instructor", is("maestro1")))
                .andExpect(jsonPath("$[2].idLesson", is(3)))
                .andExpect(jsonPath("$[2].instructor", is("maestro1")))
                .andExpect(jsonPath("$[3].idLesson", is(4)))
                .andExpect(jsonPath("$[3].instructor", is("maestro1")));
    }

    /**
     * Questo metodo testa la chiamata al endpoint getAllLessons di un utente, utilizzando JUnit e Mockito..
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/getAll/lessons", in questo caso il context di Spring Security inietta la HttpServletRequest che contiene
     * le informazioni dell'utente.Infine, viene controllato l'oggetto di risposta e confrontato con lo status (400 BadRequest),
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenLessonsUser_getAll_ReturnBadRequest() throws Exception {
        given(userService.getLessonsByUser(any())).willThrow(new EntityNotFoundException("Lezione non trovata"));
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAll/lessons"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Questo metodo testa la chiamata al endpoint registrationUserLesson di un utente, utilizzando JUnit e Mockito..
     * La lezione a cui si deve iscrivere l'utente viene mockata e a cui viene settato l'id 1.
     * Viene poi chiamato il metodo registrationLesson() presente nel service di User, ci aspettiamo che il metodo ritorno una stringa
     * poiché l'utente deve essere inserito nella lezione.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/lesson/registration" passando anche il valore dell'id della lezione.
     * Inoltre il context di Spring Security inietta la HttpServletRequest che contiene le informazioni dell'utente
     * .Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 OK), e viene controllato anche il
     * valore della stringa di risposta "Iscrizione avvenuta con successo"
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_RegistrationUserLesson_ReturnOk() throws Exception{
        Lesson lesson = Stub.getLessonsStub();
        lesson.setId(1);
        String response = "Iscrizione avvenuta con successo";
        given(userService.registrationLesson(any(),any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/lesson/registration/"+lesson.getId()))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string(response));
    }

    /**
     * Questo metodo testa la chiamata al endpoint registrationUserLesson di un utente, utilizzando JUnit e Mockito..
     * La lezione a cui si deve iscrivere l'utente viene mockata senza però passare un id valido di una lezione.
     * Viene poi chiamato il metodo registrationLesson() presente nel service di User, ci aspettiamo che il metodo sollevi
     * un'eccezione poichè la lezione non esiste.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/lesson/registration" passando anche il valore falso dell'id della lezione.
     * Inoltre il context di Spring Security inietta la HttpServletRequest che contiene le informazioni dell'utente
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (400 BadRequest).
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_RegistrationUserLesson_ReturnBadRequest() throws Exception{
        Lesson lesson = Stub.getLessonsStub();
        String response = "Iscrizione avvenuta con successo";
        given(userService.registrationLesson(any(),any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/lesson/registration/"+lesson.getId()))
                .andExpect(status().isBadRequest());
    }


    /**
     * Questo metodo testa la chiamata al endpoint enterUserPlan di un utente, utilizzando JUnit e Mockito..
     * Il metodo può essere invocato solo da ADMIN e per questo si settano le autorità nell'annotation sotto.
     * Vengono mockati un utente e un piano con dei valori corretti.
     * Viene poi chiamato il metodo insertUserPlan() presente nel service di User, e ci aspettiamo che il metodo
     * ritorni una stringa con scritto "Utente inserito correttamente".
     * Viene creato un UserPlanRequest che è il DTO reale che arriva nell'API
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/insert/plan" passando inserendo il valore di UserPlanRequest converito in una stringa
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 Ok) e viene confronto anche il valore
     * della stringa che ci aspettiamo
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenString_EnterUserPlan_ReturnOk() throws Exception {
        User user = Stub.getUserStub();
        Plan plan = Stub.getPlanStub();
        String response = "Utente inserito correttamente";
        UserPlanRequest userPlanRequest = new UserPlanRequest();
        userPlanRequest.setPlan(plan.getName());
        userPlanRequest.setEmail(user.getEmail());
        given(userService.insertUserPlan(any())).willReturn(response);
        String requestUserPlanAsString = new ObjectMapper().writeValueAsString(userPlanRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/insert/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestUserPlanAsString))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(response));
    }

    /**
     * Questo metodo testa la chiamata al endpoint enterUserPlan di un utente che avviene sbagliando i parametri
     * Il metodo può essere invocato solo da ADMIN e per questo si settano le autorità nell'annotation sotto.
     * Vengono mockati sia l'utente che un piano con dei valori sbagliati e creato un UserPlanRequest errata, affinchè il metodo
     * sollevi un IllegalStateException(). Viene quindi chiamato il metodo insertUserPlan() presente nel service di User.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/insert/plan" passando il valore della Request falso.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (409 Conflict).
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void givenString_EnterUserPlan_ReturnBadRequest() throws Exception {
        User user = Stub.getUserStub();
        Plan plan = Stub.getPlanStub();
        UserPlanRequest userPlanRequest = new UserPlanRequest();
        userPlanRequest.setPlan(plan.toString());
        userPlanRequest.setEmail(user.getEmail());
        given(userService.insertUserPlan(any())).willThrow(new IllegalStateException("errore nell'inserimento"));
        String requestUserPlanAsString = new ObjectMapper().writeValueAsString(userPlanRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/insert/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUserPlanAsString))
                .andExpect(status().isConflict());
    }

    /**
     * Questo metodo testa la chiamata al endpoint enterUserPlan di un utente che avviene sbagliando i parametri
     * Il metodo può essere invocato solo da ADMIN e per testare che questo sia vero non setto il @WithMockUser, così
     * considera la chiamata come se fosse fatta da un utente generico.
     * Vengono mockati correttamente l'utente e il piano e viene creato correttamente anche la Request
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/insert/plan" passando il valore della Request.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (403 Forbidden).
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_EnterUserPlan_ReturnUnauthorized() throws Exception {
        User user = Stub.getUserStub();
        Plan plan = Stub.getPlanStub();
        String response = "Utente inserito correttamente";
        UserPlanRequest userPlanRequest = new UserPlanRequest();
        userPlanRequest.setPlan(plan.getName());
        userPlanRequest.setEmail(user.getEmail());
        given(userService.insertUserPlan(any())).willReturn(response);
        String requestUserPlanAsString = new ObjectMapper().writeValueAsString(userPlanRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/insert/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUserPlanAsString))
                .andExpect(status().isForbidden());
    }
}
