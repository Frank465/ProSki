package com.ingegneriadelsoftware.ProSki.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingegneriadelsoftware.ProSki.DTO.Request.CommentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.InstructorRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.Mapping.Request;
import com.ingegneriadelsoftware.ProSki.Model.Instructor;
import com.ingegneriadelsoftware.ProSki.Service.InstructorService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InstructorControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private InstructorService instructorService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }


    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un istruttore.
     * Il metodo può essere invocato solo da utenti admin.
     * Viene mockatoun istruttore con i parametri corretti e successivamente si chiama il metodo insertInstructor,
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un InstructorRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato InstructorRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 200 OK e che il messaggio sia quello di response;
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void givenString_CreateInstructor_ReturnOk() throws Exception {
        Instructor instructor = Stub.getInstructorStub();
        String response = "Inserimento avvenuto con successo";
        given(instructorService.insertInstructor(any())).willReturn(response);
        InstructorRequest instructorRequest = Request.instructorRequestMapper(instructor);
        String instructorRequestAsString = new ObjectMapper().writeValueAsString(instructorRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/instructor/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(instructorRequestAsString))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(response));

    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un istruttore.
     * Il metodo può essere invocato solo da utenti admin.
     * Viene mockatoun istruttore con i parametri corretti ma successivamente si inserisce una mail di InstructoreRequest
     * errata. E successivamente si chiama il metodo insertInstructor, dal quale ci aspettiamo un errore di tipo IllegalStateexception
     * Poi viene creato un InstructorRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato InstructorRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void givenString_CreateInstructor_ReturnBadRequest() throws Exception {
        Instructor instructor = Stub.getInstructorStub();
        given(instructorService.insertInstructor(any())).willThrow(new IllegalStateException());
        InstructorRequest instructorRequest = Request.instructorRequestMapper(instructor);
        instructorRequest.setEmail("email sbagliata");
        String instructorRequestAsString = new ObjectMapper().writeValueAsString(instructorRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/instructor/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(instructorRequestAsString))
                .andExpect(status().isBadRequest());

    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un istruttore.
     * Il metodo può essere invocato solo da utenti admin.
     * Però non si definisce l'autorizzazione che ha l'utente e che quindi verrà considerato come USER
     * Viene mockatoun istruttore con i parametri corretti e successivamente si chiama il metodo insertInstructor,
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un InstructorRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato InstructorRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 403 Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateInstructor_ReturnForbidden() throws Exception {
        Instructor instructor = Stub.getInstructorStub();
        String response = "Inserimento avvenuto con successo";
        given(instructorService.insertInstructor(any())).willReturn(response);
        InstructorRequest instructorRequest = Request.instructorRequestMapper(instructor);
        String instructorRequestAsString = new ObjectMapper().writeValueAsString(instructorRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/instructor/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(instructorRequestAsString))
                .andExpect(status().isForbidden());

    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un messaggio per un istruttore.
     * Viene mockato un messagio per l'istruttore con i parametri corretti e successivamente si chiama il metodo createMessage
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un MessageRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato InstructorRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateMessageToInstructor_ReturnOk() throws Exception {
        String response = "Inserimento avvenuto con successo";
        given(instructorService.createMessage(any(), any())).willReturn(response);
        MessageRequest messageRequest = Stub.getMessageRequestStub();
        String instructorRequestAsString = new ObjectMapper().writeValueAsString(messageRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/instructor/create/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(instructorRequestAsString))
                .andExpect(status().isOk());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un messaggio per un istruttore.
     * Viene mockatoun un MessageRequest con il valore della mail dell'istruttore vuoto
     * successivamente si chiama il metodo createMessage,
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un MessageRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato MessageRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateMessageToInstructor_ReturnBadRequest() throws Exception {
        given(instructorService.createMessage(any(), any())).willThrow(new IllegalStateException());
        MessageRequest messageRequest = Stub.getMessageRequestStub();
        messageRequest.setUsername("");
        String instructorRequestAsString = new ObjectMapper().writeValueAsString(messageRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/instructor/create/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(instructorRequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un commento su un messaggio per un istruttore.
     * Viene mockato un commento per l'istruttore con i parametri corretti e successivamente si chiama il metodo createCommentToMessage
     * dal quale ci aspettiamo una stringa come risposta.
     * Poi viene creato un CommentRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato CommentRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateCommentToInstructor_ReturnOk() throws Exception {
        String response = "Inserimento avvenuto con successo";
        given(instructorService.createCommentToMessage(any(), any())).willReturn(response);
        CommentRequest commentRequest = Stub.getCommentRequestStub();
        String commentInstructorRequestAsString = new ObjectMapper().writeValueAsString(commentRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/instructor/create/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentInstructorRequestAsString))
                .andExpect(status().isOk());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la creazione di un commento su un messaggio per un istruttore.
     * Viene mockato un commento per l'istruttore con il commento come parametro vuoto e successivamente si chiama il metodo createCommentToMessage
     * dal quale ci aspettiamo un errore di Illegalstateexception
     * Poi viene creato un CommentRequest che viene convertito a stringa.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato CommentRequest come stringa.
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreateCommentToInstructor_ReturnBadRequest() throws Exception {
        given(instructorService.createCommentToMessage(any(), any())).willThrow(new IllegalStateException());
        CommentRequest commentRequest = Stub.getCommentRequestStub();
        commentRequest.setComment("");
        String commentInstructorRequestAsString = new ObjectMapper().writeValueAsString(commentRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/instructor/create/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentInstructorRequestAsString))
                .andExpect(status().isBadRequest());
    }
    /**
     * Il seguente metodo testa attraverso Mockito la get di tutti i messaggi di un istruttore.
     * Viene mockato un l'istruttore con i parametri corretti e successivamente si chiama il metodo getAllMessage
     * dal quale ci aspettiamo una stringa come risposta.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id dell'istruttore
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenMessageResponse_GetAllMessageByInstructor_ReturnOk() throws Exception {
        Instructor instructor = Stub.getInstructorStub();
        MessageResponse messageResponse = Stub.getMessageResponseStub();
        given(instructorService.getAllMessage(any())).willReturn(messageResponse);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/instructor/get/all/message/"+instructor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLocation", is(1)));
    }

    /**
     * Il seguente metodo testa attraverso Mockito la get di tutti i messaggi di un istruttore.
     * Viene mockato un l'istruttore con id nullo e successivamente si chiama il metodo getAllMessage
     * dal quale ci aspettiamo che sollevi eccezione.
     * infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id dell'istruttore
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenMessageResponse_GetAllMessageByInstructor_ReturnBadRequest() throws Exception {
        String error = "Errore nella richiesta";
        Instructor instructor = Stub.getInstructorStub();
        instructor.setId(null);
        given(instructorService.getAllMessage(any())).willThrow(new IllegalStateException(error));
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/instructor/get/all/message/"+instructor.getId()))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni messaggi di un istruttore.
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteMessage() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id dell'istruttore
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteMessageByInstructor_ReturnOk() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(instructorService.deleteMessage(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/instructor/delete/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage", is(1)));
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni messaggi di un istruttore.
     * Viene mockato un MessageDTO con i il valore dell'id errato poi viene convertito in stringa
     * Successivamente si chiama il metodo deleteMessage() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id dell'istruttore
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteMessageByInstructor_ReturnBadRequest() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        messageDTO.setIdMessage(null);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(instructorService.deleteMessage(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/instructor/delete/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni messaggi di un istruttore.
     * Il metodo può essere invocato solo da utenti admin, ma non settiamo l'autorita così da simulare un utente user
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteMessage() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id dell'istruttore
     * Alla fine ci aspettiamo che lo status della risposta sia 403 Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenMessageDTO_DeleteMessageByInstructor_ReturForbidden() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(instructorService.deleteMessage(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/instructor/delete/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isForbidden());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni commenti su un messaggio di un istruttore.
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteComments() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id dell'istruttore
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteCommentFromMessageByInstructor_ReturnOk() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(instructorService.deleteComments(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/instructor/delete/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage", is(1)));
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni commenti ad un messaggio di un istruttore.
     * Viene mockato un MessageDTO con i il valore dell'id errato poi viene convertito in stringa
     * Successivamente si chiama il metodo deleteComments() dal quale ci si aspetta l'eccezione EntityNotFound
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id dell'istruttore
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenMessageDTO_DeleteCommentFromMessageByInstructor_ReturnBadRequest() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        messageDTO.setIdMessage(null);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(instructorService.deleteComments(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/instructor/delete/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito la delete di alcuni commenti di un messaggio di un istruttore.
     * Il metodo può essere invocato solo da utenti admin, ma non settiamo l'autorita così da simulare un utente user
     * Viene mockato un MessageDTO con i parametri corretti e viene convertito in stringa
     * Successivamente si chiama il metodo deleteComments() dal quale ci si aspetta come risposta un MessageDTO
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato id dell'istruttore
     * Alla fine ci aspettiamo che lo status della risposta sia 403 Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenMessageDTO_DeleteCommentFromMessageByInstructor_ReturnForbidden() throws Exception {
        MessageDTO messageDTO = Stub.getMessageDTOListStub().get(0);
        String messageDTORequestAsString = new ObjectMapper().writeValueAsString(messageDTO);
        given(instructorService.deleteComments(any())).willReturn(messageDTO);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/instructor/delete/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageDTORequestAsString))
                .andExpect(status().isForbidden());
    }
}
