package com.ingegneriadelsoftware.ProSki.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingegneriadelsoftware.ProSki.DTO.Request.CardSkipassRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.CommentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LocationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LocationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.Mapping.Request;
import com.ingegneriadelsoftware.ProSki.Mapping.Response;
import com.ingegneriadelsoftware.ProSki.Model.CardSkipass;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Service.LocationService;
import com.ingegneriadelsoftware.ProSki.Stub;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocationControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private LocationService locationService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * Questo test verifica la corretta creazione di una location attraverso una chiamata
     * POST all'endpoint "/api/v1/location/create".
     * Viene creato un oggetto di tipo LocationRequest utilizzando uno stub di dati di prova.
     * La chiamata POST viene effettuata con successo, passando la LocationRequest come contenuto JSON
     * e ci si aspetta che lo status della risposta sia "200 OK".
     * L'annotazione @WithMockUser viene utilizzata per simulare un utente autenticato con autorità
     * di amministratore durante l'esecuzione del test.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void LocationController_CreateLocation_ReturnOk() throws Exception {
        LocationRequest locationRequest = Stub.locationDTOStub();
        Location location = new Location();
        location.setName(locationRequest.getName());
        given(locationService.createLocation(any())).willReturn(location);
        String locationRequestAsString = new ObjectMapper().writeValueAsString(locationRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationRequestAsString))
                .andExpect(status().isOk());
    }

    /**
     * Questo test verifica l'errore nella creazione di una location attraverso una chiamata
     * POST all'endpoint "/api/v1/location/create".
     * Viene creato un oggetto di tipo LocationRequest utilizzando uno stub di dati di prova che vengono falsati.
     * La chiamata POST viene effettuata con successo, passando la LocationRequest come contenuto JSON
     * e ci si aspetta che lo status della risposta sia "400 badRequest".
     * L'annotazione @WithMockUser viene utilizzata per simulare un utente autenticato con autorità
     * di amministratore durante l'esecuzione del test.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void LocationControlle_CreateLocation_ReturnBadRequest() throws Exception {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setName("");
        locationRequest.setEndOfSeason("null");
        locationRequest.setPriceSubscription(0.0);
        String locationRequestAsString = new ObjectMapper().writeValueAsString(locationRequest);
        given(locationService.createLocation(any())).willAnswer((invocation -> invocation.getArgument(0)));
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationRequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Questo test verifica la corretta creazione di una location attraverso una chiamata
     * POST all'endpoint "/api/v1/location/create".
     * Viene creato un oggetto di tipo LocationRequest utilizzando uno stub di dati di prova.
     * La chiamata POST viene effettuata con successo, passando la LocationRequest come contenuto JSON
     * e ci si aspetta che lo status della risposta sia "403 Forbidder".
     * L'annotazione @WithMockUser viene utilizzata per simulare un utente autenticato con autorità
     * di amministratore durante l'esecuzione del test, ma in questo caso non viene definita l'autorità
     * e il metodo cosidera l'utente come user sollevando, dal context security, eccezione
     *
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LocationController_CreateLocation_ReturnForbidden() throws Exception {
        LocationRequest locationRequest = Stub.locationDTOStub();
        Location location = new Location();
        location.setName(locationRequest.getName());
        given(locationService.createLocation(any())).willReturn(location);
        String locationRequestAsString = new ObjectMapper().writeValueAsString(locationRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationRequestAsString))
                .andExpect(status().isForbidden());
    }

    /**
     * Questo test verifica il corretto recupero di tutte le posizioni (locations) attraverso una chiamata GET all'endpoint "/api/v1/location/getAll".
     * Viene creato uno stub di una lista di posizioni utilizzando il metodo getLocationListStub().
     * Successivamente, viene creata una lista di LocationResponse a partire dalla lista di posizioni stub utilizzando il metodo toLocationResponseListByLocationList().
     * Viene mockato il metodo getAllLocation() del servizio locationService per restituire la lista di LocationResponse.
     * Viene eseguita una chiamata GET all'endpoint specificato.
     * Ci si aspetta che lo status della risposta sia "200 OK", indicando un recupero delle posizioni senza errori.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LocationController_GetAllLocation_ReturnOk() throws Exception {
        List<Location> locations = Stub.getLocationListStub();
        List<LocationResponse> locationResponses = Response.toLocationResponseListByLocationList(locations);
        given(locationService.getAllLocation()).willReturn(locationResponses);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/location/getAll"))
                .andExpect(status().isOk());
    }

    /**
     * Questo test verifica il caso in cui si verifichi un errore interno del server durante il recupero di tutte le locations
     * attraverso una chiamata GET all'endpoint "/api/v1/location/getAll".
     * Viene creato uno stub di una lista di posizioni utilizzando il metodo getLocationListStub().
     * Viene mockato il metodo getAllLocation() del servizio locationService per generare un'eccezione EntityNotFoundException.
     * Viene effettuata una chiamata GET all'endpoint specificato.
     * Ci si aspetta che lo status della risposta sia "500 Internal Server Error".
     *
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LocationController_GetAllLocation_ReturnInternalServerError() throws Exception {
        List<Location> locations = Stub.getLocationListStub();
        List<LocationResponse> locationResponses = Response.toLocationResponseListByLocationList(locations);
        given(locationService.getAllLocation()).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/location/getAll"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Questo test verifica la corretta creazione di uno skipass attraverso una chiamata POST all'endpoint "/api/v1/location/create/skipass".
     * Viene creato un oggetto di tipo CardSkipass utilizzando uno stub predefinito.
     * Viene impostata una risposta di conferma come stringa.
     * Viene creato un oggetto CardSkipassRequest a partire dallo skipass utilizzando un mapper specifico.
     * Viene mockato il metodo createSkipass del servizio locationService per restituire la risposta di conferma.
     * L'oggetto CardSkipassRequest viene convertito in stringa utilizzando ObjectMapper.
     * Viene eseguita una chiamata POST all'endpoint specificato, includendo la stringa dell'oggetto CardSkipassRequest come contenuto JSON.
     * Ci si aspetta che lo status della risposta sia "200 OK" e che il contenuto della risposta sia uguale alla stringa di conferma.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void LocationController_CreateSkipass_ReturnOk() throws Exception {
        CardSkipass cardSkipass = Stub.getCardSkipassStub();
        String response = "Creazione effettuta correttamente";
        CardSkipassRequest cardSkipassRequest = Request.toCardSkipassrequestByCardSkipassMapper(cardSkipass);
        given(locationService.createSkipass(any())).willReturn(response);
        String cardSkipassRequestAsString = new ObjectMapper().writeValueAsString(cardSkipassRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create/skipass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardSkipassRequestAsString))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(response));
    }

    /**
     * Verifica che venga restituito un errore di "Bad Request" durante la creazione di uno skipass, se la posizione non è specificata.
     * Utilizza uno stub di skipass e una stringa di conferma predefinita.
     * Crea un oggetto CardSkipassRequest a partire dallo skipass, ma imposta la posizione a una stringa vuota.
     * Mocka il metodo createSkipass del servizio locationService per restituire la stringa di conferma.
     * Converte l'oggetto CardSkipassRequest in formato stringa utilizzando ObjectMapper.
     * Esegue una chiamata POST all'endpoint "/api/v1/location/create/skipass",
     * includendo la stringa dell'oggetto CardSkipassRequest come contenuto JSON.
     * Ci si aspetta che lo status della risposta sia "400 Bad Request".
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void LocationController_CreateSkipass_ReturnBadRequest() throws Exception {
        CardSkipass cardSkipass = Stub.getCardSkipassStub();
        String response = "Creazione effettuta correttamente";
        CardSkipassRequest cardSkipassRequest = Request.toCardSkipassrequestByCardSkipassMapper(cardSkipass);
        cardSkipassRequest.setLocation("");
        given(locationService.createSkipass(any())).willReturn(response);
        String cardSkipassRequestAsString = new ObjectMapper().writeValueAsString(cardSkipassRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create/skipass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardSkipassRequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Questo test verifica la corretta creazione di uno skipass attraverso una chiamata POST all'endpoint "/api/v1/location/create/skipass".
     * Viene creato un oggetto di tipo CardSkipass utilizzando uno stub predefinito.
     * Viene impostata una risposta di conferma come stringa.
     * Viene creato un oggetto CardSkipassRequest a partire dallo skipass utilizzando un mapper specifico.
     * Viene mockato il metodo createSkipass del servizio locationService per restituire la risposta di conferma.
     * L'oggetto CardSkipassRequest viene convertito in stringa utilizzando ObjectMapper.
     * Viene eseguita una chiamata POST all'endpoint specificato, includendo la stringa dell'oggetto CardSkipassRequest come contenuto JSON.
     * Ci si aspetta che lo status della risposta sia "403 Forbidden" per via che il metodo è autorizzato al solo amministartore
     * e non sono stati settati i parametri nel MockUser e quindi l'untete viene considerato user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void LocationController_CreateSkipass_ReturnForbidden() throws Exception {
        CardSkipass cardSkipass = Stub.getCardSkipassStub();
        String response = "Creazione effettuta correttamente";
        CardSkipassRequest cardSkipassRequest = Request.toCardSkipassrequestByCardSkipassMapper(cardSkipass);
        given(locationService.createSkipass(any())).willReturn(response);
        String cardSkipassRequestAsString = new ObjectMapper().writeValueAsString(cardSkipassRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create/skipass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardSkipassRequestAsString))
                .andExpect(status().isForbidden());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un messaggio per una location.
     * Viene mockato un messagio per la location con i parametri corretti e successivamente si chiama il metodo createMessage
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un MessageRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato LocationRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     *
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateMessageToLocation_ReturnOk() throws Exception {
        String response = "Inserimento avvenuto con successo";
        given(locationService.createMessage(any(), any())).willReturn(response);
        MessageRequest messageRequest = Stub.getMessageRequestStub();
        String locationRequestAsString = new ObjectMapper().writeValueAsString(messageRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationRequestAsString))
                .andExpect(status().isOk());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un messaggio per una location
     * Viene mockatoun un MessageRequest con il valore della nome della località vuoto
     * successivamente si chiama il metodo createMessage,
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un MessageRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato MessageRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     *
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateMessageToLocation_ReturnBadRequest() throws Exception {
        given(locationService.createMessage(any(), any())).willThrow(new IllegalStateException());
        MessageRequest messageRequest = Stub.getMessageRequestStub();
        messageRequest.setUsername("");
        String locationRequestAsString = new ObjectMapper().writeValueAsString(messageRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationRequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un commento su un messaggio per una location
     * Viene mockato un commento per l'istruttore con i parametri corretti e successivamente si chiama il metodo createCommentToMessage
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un CommentRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato CommentRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     *
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateCommentToLocation_ReturnOk() throws Exception {
        String response = "Inserimento avvenuto con successo";
        given(locationService.createCommentToMessage(any(), any())).willReturn(response);
        CommentRequest commentRequest = Stub.getCommentRequestStub();
        String commentLocationRequestAsString = new ObjectMapper().writeValueAsString(commentRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentLocationRequestAsString))
                .andExpect(status().isOk());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un commento su un messaggio per una location.
     * Viene mockato un commento per la localita con il commento come parametro vuoto e successivamente si chiama il metodo createCommentToMessage
     * dal quale ci aspettiamo un errore di Illegalstateexception
     * Poi viene creato un CommentRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato CommentRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     *
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateCommentToLocation_ReturnBadRequest() throws Exception {
        given(locationService.createCommentToMessage(any(), any())).willThrow(new IllegalStateException());
        CommentRequest commentRequest = Stub.getCommentRequestStub();
        commentRequest.setComment("");
        String commentLocationRequestAsString = new ObjectMapper().writeValueAsString(commentRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/location/create/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentLocationRequestAsString))
                .andExpect(status().isBadRequest());
    }

    @GetMapping("/get/all/message/{id_location}")
    public ResponseEntity<MessageResponse> gelAllMessageByLocation(@PathVariable("id_location") Integer idLocation) {
        try{
            return ResponseEntity.ok(locationService.getAllMessage(idLocation));
        } catch(IllegalStateException e) {
            return new ResponseEntity<>(MessageResponse.builder().error(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Verifica il recupero di tutti i messaggi associati a una determinata posizione attraverso una chiamata
     * GET all'endpoint "/api/v1/location/get/all/message/{locationId}".Utilizza uno stub di location e di messaggio di risposta.
     * Mocka il metodo getAllMessage del servizio locationService per restituire il messaggio di risposta.
     * Esegue una chiamata GET all'endpoint specificato, includendo l'ID della posizione come parte dell'URL.
     * Ci si aspetta che lo status della risposta sia "200 OK".
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_GetAllMessageLocation_ReturnOk() throws Exception {
        Location location = Stub.getLocationStub();
        MessageResponse messageResponse = Stub.getMessageResponseStub();
        given(locationService.getAllMessage(any())).willReturn(messageResponse);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/location/get/all/message/"+ location.getLocationId()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica il caso in cui si verifichi un errore durante il recupero di tutti i messaggi associati a una determinata posizione,
     * attraverso una chiamata GET all'endpoint "/api/v1/location/get/all/message/{locationId}". Utilizza uno stub di location.
     * Mocka il metodo getAllMessage del servizio locationService per generare un'eccezione IllegalStateException.
     * Esegue una chiamata GET all'endpoint specificato, includendo l'ID della posizione come parte dell'URL.
     * Ci si aspetta che lo status della risposta sia "400 Bad Request".
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_GetAllMessageLocation_ReturnBadRequest() throws Exception {
        Location location = Stub.getLocationStub();
        given(locationService.getAllMessage(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/location/get/all/message/"+ location.getLocationId()))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni messaggi di una.
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteMessage() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id della location
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteMessageByLocation_ReturnOk() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(locationService.deleteMessage(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/location/delete/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage", is(1)));
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni messaggi di una location.
     * Viene mockato un MessageDTO con i il valore dell'id errato poi viene convertito in stringa
     * Successivamente si chiama il metodo deleteMessage() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id della location
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteMessageByLocation_ReturnBadRequest() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        messageDTO.setIdMessage(null);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(locationService.deleteMessage(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/location/delete/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni messaggi di una location
     * Il metodo può essere invocato solo da utenti admin, ma non settiamo l'autorita così da simulare un utente user
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteMessage() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id della location
     * Alla fine ci aspettiamo che lo status della risposta sia 403 Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenMessageDTO_DeleteMessageByLocation_ReturForbidden() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(locationService.deleteMessage(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/instructor/delete/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isForbidden());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni commenti su un messaggio di una location
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteComments() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id della location
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteCommentFromMessageByLocation_ReturnOk() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(locationService.deleteComments(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/location/delete/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage", is(1)));
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni commenti ad un messaggio di una location
     * Viene mockato un MessageDTO con i il valore dell'id errato poi viene convertito in stringa
     * Successivamente si chiama il metodo deleteComments() dal quale ci si aspetta l'eccezione EntityNotFound
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id della location
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteCommentFromMessageByInstructor_ReturnBadRequest() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        messageDTO.setIdMessage(null);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(locationService.deleteComments(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/location/delete/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni commenti di un messaggio di una location
     * Il metodo può essere invocato solo da utenti admin, ma non settiamo l'autorita così da simulare un utente user
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteComments() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id della location
     * Alla fine ci aspettiamo che lo status della risposta sia 403 Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenMessageDTO_DeleteCommentFromMessageByLocation_ReturnForbidden() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(locationService.deleteComments(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/location/delete/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isForbidden());
    }
}


