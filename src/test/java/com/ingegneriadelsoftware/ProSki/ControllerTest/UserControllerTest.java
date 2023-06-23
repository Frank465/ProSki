package com.ingegneriadelsoftware.ProSki.ControllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.BuySkipassRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.ReservationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.UserPlanRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LessonResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.ReservationResponse;
import com.ingegneriadelsoftware.ProSki.Mapping.Request;
import com.ingegneriadelsoftware.ProSki.Mapping.Response;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Service.UserService;
import com.ingegneriadelsoftware.ProSki.Stub;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    /**
     * Questo metodo testa la chiamata al endpoint buySkipass di un utente, utilizzando JUnit e Mockito.
     * Vengono mockati un utente e un piano con dei valori corretti.
     * Viene poi chiamato il metodo buySkipassUser() presente nel service di User, e ci aspettiamo che il metodo
     * ritorni una ResponseEntity.
     * Viene creato un BuySkipassRequest che è il DTO reale che arriva nell'API
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/buy/skipass" passando inserendo il valore di BuySkipassRequest converito in una stringa
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 Ok)
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenBuySkipassResponse_BuySkipass_returnOk() throws Exception{
        BuySkipass buySkipass = Stub.getBuySkipass();
        BuySkipassRequest request = Request.buySkipassRequestMapper(buySkipass);
        given(userService.buySkipassUser(any(), any())).willReturn(buySkipass);
        String requestBuySkipasasString = new ObjectMapper().writeValueAsString(request);
        System.out.println(requestBuySkipasasString);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/buy/skipass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBuySkipasasString))
                .andExpect(status().isOk());
    }

    /**
     * Questo metodo testa la chiamata al endpoint buySkipass di un utente che avviene sbagliando i parametri
     * Vengono mockati sia l'utente che un piano con dei valori sbagliati e creato un BuySkipassRequest errata, affinchè il metodo
     * sollevi un IllegalStateException(). Viene quindi chiamato il metodo buySkipassUser() presente nel service di User.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/buy/skipass" passando il valore della Request falso.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (400 BadRequest).
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenBuySkipassResponse_BuySkipass_returnBadRequest() throws Exception{
        BuySkipass buySkipass = Stub.getBuySkipass();
        BuySkipassRequest request = Request.buySkipassRequestMapper(buySkipass);
        request.setDate("data errata");
        given(userService.buySkipassUser(any(), any())).willThrow(new IllegalStateException());
        String requestBuySkipasasString = new ObjectMapper().writeValueAsString(request);
        System.out.println(requestBuySkipasasString);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/buy/skipass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBuySkipasasString))
                .andExpect(status().isBadRequest());
    }



    /**
     * Questo metodo testa la chiamata al endpoint GetUsersByGender di un utente, utilizzando JUnit e Mockito.
     * Vengono mockati un utente e un piano con dei valori corretti.
     * Viene poi chiamato il metodo getAllUsersByGender() presente nel service di User, e ci aspettiamo che il metodo
     * ritorni una ResponseEntity<>.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/getAllUsers/byGender" passando il valore di gender
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 Ok)
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenAnythings_GetUsersByGender_ReturnOk() throws Exception{
        List<User> userList = Stub.getUserListStub();
        String gender = "women";
        given(userService.getAllUsersByGender(any())).willReturn(userList);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAllUsers/byGender/" + gender))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].name", is("user1")))
                .andExpect(jsonPath("$[1].userId", is(2)))
                .andExpect(jsonPath("$[1].name", is("user2")));
    }

    /**
     * Questo metodo testa la chiamata al endpoint GetUsersByGender di un utente che avviene sbagliando i parametri
     * Vien passato alla chiamata un valore di gender falso affinchè il metodo sollevi un IllegalStateException(). Viene quindi chiamato il metodo buySkipassUser() presente nel service di User.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/getAllUsers/byGender/" passando il valore della stringa gender falso.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (400 BadRequest).
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenAnythings_GetUsersByGender_ReturnInternalServerError() throws Exception{
        String gender = "errore";
        given(userService.getAllUsersByGender(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAllUsers/byGender/" + gender))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Questo metodo testa la chiamata al endpoint GetUsersByGender di un utente
     * Il metodo può essere invocato solo da ADMIN e per testare che questo sia vero non setto il @WithMockUser, così
     * considera la chiamata come se fosse fatta da un utente generico.
     * Vengono mockati correttamente gli utenti e viene passato un valore corretto alla request
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/getAllUsers/byGender/" passando il valore della stringa gender.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (403 Forbidden).
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenAnythings_GetUsersByGender_ReturnForbidden() throws Exception{
        List<User> userList = Stub.getUserListStub();
        String gender = "women";
        given(userService.getAllUsersByGender(any())).willReturn(userList);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAllUsers/byGender/" + gender))
                .andExpect(status().isForbidden());
    }


    /**
     * Questo metodo testa la chiamata al endpoint GetUsersByAge di un utente, utilizzando JUnit e Mockito.
     * Vengono mockati un utente e un piano con dei valori corretti.
     * Viene poi chiamato il metodo getUsersByAgeBetween() presente nel service di User, e ci aspettiamo che il metodo
     * ritorni una ResponseEntity<>.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/getAllUsers/byAge" passando il valore delle età di inizio e di fine
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 Ok)
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenAnythings_GetUsersByAge_ReturnOk() throws Exception{
        List<User> userList = Stub.getUserListStub();
        Integer startAge = 10;
        Integer endAge = 14;
        given(userService.getUsersByAgeBetween(any(), any())).willReturn(userList);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAllUsers/byAge?startAge" + startAge +"&endAge=" + endAge ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].name", is("user1")))
                .andExpect(jsonPath("$[1].userId", is(2)))
                .andExpect(jsonPath("$[1].name", is("user2")));
    }

    /**
     * Questo metodo testa la chiamata al endpoint GetUsersByAge di un utente che avviene sbagliando i parametri
     * Vien passato alla chiamata i valori di inizio e fine data non validi(temporalmente) affinchè il metodo sollevi un IllegalStateException(). Viene quindi chiamato il metodo buySkipassUser() presente nel service di User.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/getAllUsers/byAge/" passando il valore degli Integer falsi.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (400 BadRequest).
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenAnythings_GetUsersByAge_ReturnBadRequest() throws Exception{
        Integer startAge = 45;
        Integer endAge = 11;
        given(userService.getUsersByAgeBetween(any(), any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAllUsers/byAge?startAge"
                        + startAge +"&endAge=" + endAge ))
                .andExpect(status().isBadRequest());
    }

    /**
     * Questo metodo testa la chiamata al endpoint GetUsersByAge di un utente
     * Il metodo può essere invocato solo da ADMIN e per testare che questo sia vero non setto il @WithMockUser, così
     * considera la chiamata come se fosse fatta da un utente generico.
     * Vengono mockati correttamente i valori di inizio e fine età
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/getAllUsers/byAge/" passando i valori di inizio e fine età
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (403 Forbidden).
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenAnythings_GetUsersByAge_ReturnForbidden() throws Exception{
        List<User> userList = Stub.getUserListStub();
        Integer startAge = 10;
        Integer endAge = 14;
        given(userService.getUsersByAgeBetween(any(), any())).willReturn(userList);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAllUsers/byAge?startAge"
                        + startAge +"&endAge=" + endAge))
                .andExpect(status().isForbidden());
    }



    /**
     * Questo metodo testa la chiamata al endpoint deleteUtente di un utente, utilizzando JUnit e Mockito.
     * Viene mockato un utente con dei valori corretti.
     * Viene poi chiamato il metodo deleteUserByEmail() presente nel service di User, e ci aspettiamo che il metodo
     * ritorni una stringa.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/delete/user/" passando il valore corretto della mail di un utente
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 Ok) e il valore della stringa di ritorno
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenString_DeleteUtente_ReturnOk() throws Exception {
        User user = Stub.getUserStub();
        String response = "utente eliminato correttamente";
        given(userService.deleteUserByEmail(any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/delete/user/"+user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(response));
    }

    /**
     * Questo metodo testa la chiamata al endpoint deleteUtente di un utente che avviene sbagliando i parametri
     * Viene mockato uno user con una email non valida
     * Viene passato alla chiamata il valore della email non valido affinchè il metodo sollevi un IllegalStateException(). Viene quindi chiamato il metodo buySkipassUser() presente nel service di User.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/delete/user/" passando il valore della email falsi.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (500 internalServerError).
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenString_DeleteUtente_ReturnInternalServerError() throws Exception {
        User user = Stub.getUserStub();
        user.setEmail("errore email ");
        given(userService.deleteUserByEmail(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/delete/user/"+user.getEmail()))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Questo metodo testa la chiamata al endpoint deleteUtente di un utente
     * Il metodo può essere invocato solo da ADMIN e per testare che questo sia vero non setto il @WithMockUser, così
     * considera la chiamata come se fosse fatta da un utente generico.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/delete/user/" passando il valore della mail corretto che è quello mockato in utente
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (403 Forbidden).
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_DeleteUtente_ReturnForbidden() throws Exception {
        User user = Stub.getUserStub();
        String response = "utente eliminato correttamente";
        given(userService.deleteUserByEmail(any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/delete/user/"+user.getEmail()))
                .andExpect(status().isForbidden());
    }

    /**
     * Questo metodo testa la chiamata al endpoint createReservation di un utente, utilizzando JUnit e Mockito.
     * Viene mockato una prenotazione con dei valori corretti e generati i mock per la ReservationRequest e la
     * ReservationResponse.
     * Viene poi chiamato il metodo createReservation() presente nel service di User, e ci aspettiamo che il metodo
     * ritorni una Reservation.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/reservation/create" passando il valore corretto della reservationRequest come stringa
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 Ok) e alcuni valori
     * della ReservationResponse di ritorno
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenReservationResponse_CreateReservation_ReturnOk() throws Exception {
        Reservation reservation = Stub.getReservationStub();
        ReservationResponse reservationResponse = Response.toReservationResponseByReservationMapper(reservation);
        ReservationRequest reservationRequest = Request.reservationRequestMapper(reservation);
        System.out.println(reservationRequest);
        given(userService.createReservation(any(), any())).willReturn(reservation);
        String reservationRequestAsString = new ObjectMapper().writeValueAsString(reservationRequest);
        System.out.println(reservationRequestAsString);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationRequestAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is(reservationResponse.getUserName())))
                .andExpect(jsonPath("$.vendorName", is(reservationResponse.getVendorName())));
    }

    /**
     * Questo metodo testa la chiamata al endpoint createReservation di un utente, utilizzando JUnit e Mockito.
     * Viene mockato una prenotazione con il valore di una lista di sci errato, mentre i valori corretti per la
     * ReservationResponse sono giusti.
     * Viene poi chiamato il metodo createReservation() presente nel service di User, e ci aspettiamo che il metodo
     * sollevi l'eccezione IllegalStateException
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/reservation/create" passando il valore errato della reservationRequest come stringa
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (400 BadRequest)
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenReservationResponse_CreateReservation_ReturnBadRequest() throws Exception {
        Reservation reservation = Stub.getReservationStub();
        ReservationRequest reservationRequest = Request.reservationRequestMapper(reservation);
        reservationRequest.setStartDate("data errata");
        given(userService.createReservation(any(), any())).willThrow(new IllegalStateException());
        String reservationRequestAsString = new ObjectMapper().writeValueAsString(reservationRequest);
        System.out.println(reservationRequestAsString);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/reservation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationRequestAsString))
                .andExpect(status().isBadRequest());

    }
}
