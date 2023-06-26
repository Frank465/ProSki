package com.ingegneriadelsoftware.ProSki.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingegneriadelsoftware.ProSki.DTO.Request.*;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.Mapping.Request;
import com.ingegneriadelsoftware.ProSki.Model.Vendor;
import com.ingegneriadelsoftware.ProSki.Service.VendorService;
import com.ingegneriadelsoftware.ProSki.Stub;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;

import java.time.DateTimeException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VendorControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private VendorService vendorService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * Questo test verifica che l'endpoint per la creazione di un fornitore restituisca lo stato "OK" (200)
     * Viene simulata una richiesta POST all'endpoint "/api/v1/vendor/create"
     * con un corpo di richiesta JSON che rappresenta il fornitore da creare.
     * L'implementazione del servizio del fornitore (vendorService) viene sostituita con un mock
     * Il metodo che viene testato presuppone l'autenticazione dell'utente admin come correttamente inserito
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void VendorController_CreateVendor_ReturnOk() throws Exception {
        Vendor vendor = Stub.getVendorStub();
        VendorRequest vendorRequest = Request.toVendorRequestByVendorMapper(vendor);
        String response = "Inserimento avvenuto con successo";
        String vendorRequestAsString = new ObjectMapper().writeValueAsString(vendorRequest);
        given(vendorService.insertVendor(any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vendorRequestAsString))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(response));
    }

    /**
     * Questo test verifica che l'endpoint per la creazione di un fornitore restituisca lo stato "BadRequest" (400)
     * Viene simulata una richiesta POST all'endpoint "/api/v1/vendor/create"
     * con un corpo di richiesta JSON che rappresenta il fornitore da creare che però ha l'email errata.
     * L'implementazione del servizio del fornitore (vendorService) viene sostituita con un mock
     * Il metodo che viene testato presuppone l'autenticazione dell'utente admin come correttamente inserito
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void VendorController_CreateVendor_ReturnBadRequest() throws Exception {
        Vendor vendor = Stub.getVendorStub();
        VendorRequest vendorRequest = Request.toVendorRequestByVendorMapper(vendor);
        vendorRequest.setEmail("email sbagliata");
        String vendorRequestAsString = new ObjectMapper().writeValueAsString(vendorRequest);
        given(vendorService.insertVendor(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vendorRequestAsString))
                .andExpect(status().isBadRequest());
    }
    /**
     * Questo test verifica che l'endpoint per la creazione di un fornitore restituisca lo stato "Forbidden" (403)
     * Viene simulata una richiesta POST all'endpoint "/api/v1/vendor/create"
     * con un corpo di richiesta JSON che rappresenta il fornitore da creare.
     * L'implementazione del servizio del fornitore (vendorService) viene sostituita con un mock
     * Il metodo che viene testato presuppone l'autenticazione dell'utente admin, ma non è stato settato il valore
     * dell'utente che quindi user
     */
    @Test
    @WithMockUser
    public void VendorController_CreateVendor_ReturnForbidden() throws Exception {
        Vendor vendor = Stub.getVendorStub();
        VendorRequest vendorRequest = Request.toVendorRequestByVendorMapper(vendor);
        String response = "Inserimento avvenuto con successo";
        String vendorRequestAsString = new ObjectMapper().writeValueAsString(vendorRequest);
        given(vendorService.insertVendor(any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vendorRequestAsString))
                .andExpect(status().isForbidden());
    }

    /**
     * Questo test verifica che l'endpoint per l'aggiornamento dell' equipaggiamento di un fornitore restituisca lo stato "OK" (200)
     * quando viene chiamato con un utente autenticato come amministratore.
     * Viene simulata una richiesta POST all'endpoint "/api/v1/vendor/insert/equipment" con un corpo di richiesta JSON
     * che rappresenta l'equipaggiamento del fornitore da aggiornare. L'implementazione del servizio del fornitore (vendorService)
     * viene sostituita con un mock che restituisce una risposta di successo (200Ok)
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void VendorController_UpdateVendorEquipment_ReturnOk() throws Exception {
        VendorEquipmentRequest vendorEquipmentRequest = Stub.getVendorEquipmentRequestStub();
        String response = "Inserimento avvenuto con successo";
        String vendorEquipmentRequestAsString = new ObjectMapper().writeValueAsString(vendorEquipmentRequest);
        given(vendorService.insertVendor(any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/insert/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vendorEquipmentRequestAsString))
                .andExpect(status().isOk());
    }

    /**
     * Viene simulata una richiesta POST all'endpoint "/api/v1/vendor/insert/equipment"
     * con un corpo di richiestaJSON
     * che rappresenta l'equipaggiamento del fornitore da aggiornare ma con il valore di email vendor sbagliato.
     * L'implementazione del servizio del fornitore (vendorService)
     * viene sostituita con un mock che restituisce una risposta di fallimento (400 BadRequest),
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void VendorController_UpdateVendorEquipment_ReturnBadRequest() throws Exception {
        VendorEquipmentRequest vendorEquipmentRequest = Stub.getVendorEquipmentRequestStub();
        String response = "Inserimento avvenuto con successo";
        vendorEquipmentRequest.setVendorEmail("email sbagliata");
        String vendorEquipmentRequestAsString = new ObjectMapper().writeValueAsString(vendorEquipmentRequest);
        given(vendorService.insertVendor(any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/insert/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vendorEquipmentRequestAsString))
                .andExpect(status().isBadRequest());
    }


    /**
     * Viene simulata una richiesta POST all'endpoint "/api/v1/vendor/insert/equipment" con un corpo di richiesta JSON
     * che rappresenta l'equipaggiamento del fornitore da aggiornare. L'implementazione del servizio del fornitore (vendorService)
     * viene sostituita con un mock che restituisce una risposta di fallimento (403Forbidden), poichè non è stato inserito
     * l'utente admin che l'endPoint si aspettava
     */
    @Test
    @WithMockUser
    public void VendorController_UpdateVendorEquipment_ReturnForbidden() throws Exception {
        VendorEquipmentRequest vendorEquipmentRequest = Stub.getVendorEquipmentRequestStub();
        String response = "Inserimento avvenuto con successo";
        String vendorEquipmentRequestAsString = new ObjectMapper().writeValueAsString(vendorEquipmentRequest);
        given(vendorService.insertVendor(any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/insert/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vendorEquipmentRequestAsString))
                .andExpect(status().isForbidden());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un messaggio per un vendor.
     * Viene mockato un messagio per il vendor con i parametri corretti e successivamente si chiama il metodo createMessage
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un MessageRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato InstructorRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateMessageToVendor_ReturnOk() throws Exception {
        String response = "Inserimento avvenuto con successo";
        given(vendorService.createMessage(any(), any())).willReturn(response);
        MessageRequest messageRequest = Stub.getMessageRequestStub();
        String instructorRequestAsString = new ObjectMapper().writeValueAsString(messageRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/create/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(instructorRequestAsString))
                .andExpect(status().isOk());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un messaggio per un vendor.
     * Viene mockatoun un MessageRequest con il valore della mail del vendor vuoto
     * successivamente si chiama il metodo createMessage,
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un MessageRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato MessageRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateMessageToVendor_ReturnBadRequest() throws Exception {
        given(vendorService.createMessage(any(), any())).willThrow(new IllegalStateException());
        MessageRequest messageRequest = Stub.getMessageRequestStub();
        messageRequest.setUsername("");
        String instructorRequestAsString = new ObjectMapper().writeValueAsString(messageRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/create/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(instructorRequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un commento su un messaggio per un vendor.
     * Viene mockato un commento per il vendor con i parametri corretti e successivamente si chiama il metodo createCommentToMessage
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un CommentRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato CommentRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateCommentToVendor_ReturnOk() throws Exception {
        String response = "Inserimento avvenuto con successo";
        given(vendorService.createCommentToMessage(any(), any())).willReturn(response);
        CommentRequest commentRequest = Stub.getCommentRequestStub();
        String commentInstructorRequestAsString = new ObjectMapper().writeValueAsString(commentRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/create/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentInstructorRequestAsString))
                .andExpect(status().isOk());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un commento su un messaggio per un vendor.
     * Viene mockato un commento per il vendor con il commento come parametro vuoto e successivamente si chiama il metodo createCommentToMessage
     * dal quale ci aspettiamo un errore di Illegalstateexception
     * Poi viene creato un CommentRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato CommentRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateCommentToVendor_ReturnBadRequest() throws Exception {
        given(vendorService.createCommentToMessage(any(), any())).willThrow(new IllegalStateException());
        CommentRequest commentRequest = Stub.getCommentRequestStub();
        commentRequest.setComment("");
        String commentInstructorRequestAsString = new ObjectMapper().writeValueAsString(commentRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/vendor/create/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentInstructorRequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * EndPoint da chiamare prima di creare una prenotazione, per prendere la lista delle attrezzature del rifornitore
     * @param request
     * @return
     */
    @GetMapping("/getEquipmentAvailable/vendor")
    public ResponseEntity<EquipmentAvailableResponse> getEquipmentAvailableByVendor(@Valid @RequestBody EquipementVendorAvailable request) {
        try{
            return ResponseEntity.ok(vendorService.getEquipmentAvailableForDate(request.getVendorEmail(), Utils.formatterData(request.getStartDate()), Utils.formatterData(request.getEndDate())));
        }catch (IllegalStateException | DateTimeException ex) {
            return new ResponseEntity<>(EquipmentAvailableResponse.builder().message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Questo test verifica che la chiamata API per ottenere gli equipaggiamenti disponibili per un determinato
     * venditore restituisca uno stato HTTP 200 (OK). Il test utilizza un utente fittizio autenticato (@WithMockUser)
     * e configura le risposte stub per il venditore e il messaggio. Viene creato un oggetto equipementVendorAvailable
     * che rappresenta i dati degli equipaggiamenti disponibili per il venditore e viene convertito in formato JSON.
     * Viene quindi impostato il comportamento stub del vendorService per restituire il messageResponse specificato.
     * Successivamente, viene effettuata una richiesta GET all'endpoint "/api/v1/vendor/getEquipmentAvailable/vendor"
     * con il corpo contenente l'equipementVendorAvailable convertito in JSON.
     * Il test verifica che lo stato della risposta sia 200 (isOk()).
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenEquipmentAvailableResponse_getEquipmentAvailableByVendor_ReturnOk() throws Exception {
        EquipementVendorAvailable equipementVendorAvailable = Stub.getEquipementVendorAvailableStub();
        MessageResponse messageResponse = Stub.getMessageResponseStub();
        String equipementVendorAvailableAsString = new ObjectMapper().writeValueAsString(equipementVendorAvailable);
        given(vendorService.getAllMessage(any())).willReturn(messageResponse);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor/getEquipmentAvailable/vendor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipementVendorAvailableAsString))
                .andExpect(status().isOk());
    }

    /**
     * Questo test verifica che la chiamata API per ottenere gli equipaggiamenti disponibili per un determinato
     * venditore restituisca uno stato HTTP 400 (Bad Request) nel caso in cui l'email del venditore sia vuota.
     * Il test utilizza un utente fittizio autenticato (@WithMockUser) e configura le risposte stub per il venditore e il messaggio.
     * Viene creato un oggetto equipementVendorAvailable che rappresenta i dati degli equipaggiamenti disponibili
     * per il venditore e viene impostata la sua email come vuota per generare un caso di richiesta non valida.
     * L'oggetto equipementVendorAvailable viene quindi convertito in formato JSON. Viene impostato il comportamento
     * stub del vendorService per restituire il messageResponse specificato.
     * Successivamente, viene effettuata una richiesta GET all'endpoint "/api/v1/vendor/getEquipmentAvailable/vendor"
     * con il corpo contenente l'equipementVendorAvailable convertito in JSON. Il test verifica che lo stato
     * della risposta sia 400 (isBadRequest()).
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenEquipmentAvailableResponse_getEquipmentAvailableByVendor_ReturnBadRequest() throws Exception {
        EquipementVendorAvailable equipementVendorAvailable = Stub.getEquipementVendorAvailableStub();
        equipementVendorAvailable.setVendorEmail("");
        MessageResponse messageResponse = Stub.getMessageResponseStub();
        String equipementVendorAvailableAsString = new ObjectMapper().writeValueAsString(equipementVendorAvailable);
        given(vendorService.getAllMessage(any())).willReturn(messageResponse);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor/getEquipmentAvailable/vendor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipementVendorAvailableAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica il recupero di tutti i messaggi associati a un determinato vendor attraverso una chiamata
     * GET all'endpoint "/api/v1/location/get/all/message/{vendorId}".Utilizza uno stub di vendor e di messaggio di risposta.
     * Mocka il metodo getAllMessage del servizio locationService per restituire il messaggio di risposta.
     * Esegue una chiamata GET all'endpoint specificato, includendo l'ID del vendor come parte dell'URL.
     * Ci si aspetta che lo status della risposta sia "200 OK".
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_GetAllMessageVendor_ReturnOk() throws Exception {
        Vendor vendor = Stub.getVendorStub();
        MessageResponse messageResponse = Stub.getMessageResponseStub();
        given(vendorService.getAllMessage(any())).willReturn(messageResponse);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor/get/all/message/"+ vendor.getVendorId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(messageResponse.getUsername())));
    }

    /**
     * Verifica il caso in cui si verifichi un errore durante il recupero di tutti i messaggi associati a un determinato vendor,
     * attraverso una chiamata GET all'endpoint "/api/v1/location/get/all/message/{vendorId}". Utilizza uno stub di vendor.
     * Mocka il metodo getAllMessage del servizio vendorService per generare un'eccezione IllegalStateException.
     * Esegue una chiamata GET all'endpoint specificato, includendo l'ID del vendor come parte dell'URL.
     * Ci si aspetta che lo status della risposta sia "400 Bad Request".
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_GetAllMessageVendor_ReturnBadRequest() throws Exception {
        Vendor vendor = Stub.getVendorStub();
        vendor.setVendorId(null);
        given(vendorService.getAllMessage(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor/get/all/message/"+ vendor.getVendorId()))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni messaggi di una.
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteMessage() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id del vendor
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteMessageByLocation_ReturnOk() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(vendorService.deleteMessage(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/vendor/delete/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage", is(1)));
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni messaggi di un vendor.
     * Viene mockato un MessageDTO con i il valore dell'id errato poi viene convertito in stringa
     * Successivamente si chiama il metodo deleteMessage() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id del vendor
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteMessageByLocation_ReturnBadRequest() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        messageDTO.setIdMessage(null);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(vendorService.deleteMessage(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/vendor/delete/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni messaggi di un vendor
     * Il metodo può essere invocato solo da utenti admin, ma non settiamo l'autorita così da simulare un utente user
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteMessage() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id del vendor
     * Alla fine ci aspettiamo che lo status della risposta sia 403 Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenMessageDTO_DeleteMessageByLocation_ReturForbidden() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(vendorService.deleteMessage(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/vendor/delete/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isForbidden());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni commenti su un messaggio di un vendor
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteComments() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id del vendor
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteCommentFromMessageByVendor_ReturnOk() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(vendorService.deleteComments(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/vendor/delete/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage", is(1)));
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni commenti ad un messaggio di un vendor
     * Viene mockato un MessageDTO con i il valore dell'id errato poi viene convertito in stringa
     * Successivamente si chiama il metodo deleteComments() dal quale ci si aspetta l'eccezione EntityNotFound
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id del vendor
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteCommentFromMessageByVendor_ReturnBadRequest() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        messageDTO.setIdMessage(null);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(vendorService.deleteComments(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/vendor/delete/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni commenti di un messaggio di un vendor
     * Il metodo può essere invocato solo da utenti admin, ma non settiamo l'autorita così da simulare un utente user
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteComments() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id del vendor
     * Alla fine ci aspettiamo che lo status della risposta sia 403 Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenMessageDTO_DeleteCommentFromMessageByVendor_ReturnForbidden() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(vendorService.deleteComments(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/vendor/delete/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isForbidden());
    }
}
