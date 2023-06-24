package com.ingegneriadelsoftware.ProSki.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.OfferRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.PlanRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.OfferResponse;
import com.ingegneriadelsoftware.ProSki.Mapping.Request;
import com.ingegneriadelsoftware.ProSki.Model.Offer;
import com.ingegneriadelsoftware.ProSki.Model.Plan;
import com.ingegneriadelsoftware.ProSki.Service.PlaneOfferService;
import com.ingegneriadelsoftware.ProSki.Stub;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanOfferControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private PlaneOfferService planeOfferService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * Il seguente metodo testa attraverso Mockito l'inserimento di un piano
     * Viene mockato un Plan con i parametri corretti e viene successivamente a partire da questo creato
     * un PlanRequest e convertito in stringa
     * Successivamente si chiama il metodo createPlan() dal quale ci si aspetta come risposta di tipo stringa
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato il PlanRequest
     * Alla fine ci aspettiamo che lo status della risposta sia 200 Ok
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenString_CreatePlan_ReturnOk() throws Exception {
        Plan plan = Stub.getPlanStub();
        String response = "Piano inserito correttamente";
        PlanRequest planRequest = new PlanRequest();
        planRequest.setPlanName(plan.getName());
        given(planeOfferService.createPlan(any())).willReturn(response);
        String planRequestAsString = new ObjectMapper().writeValueAsString(planRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/planOffer/create/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(planRequestAsString))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(response));
    }

    /**
     * Il seguente metodo testa attraverso Mockito l'inserimento di un piano
     * Viene mockato un Plan con il nome vuoto e viene successivamente a partire da questo creato
     * un PlanRequest e convertito in stringa
     * Successivamente si chiama il metodo createPlan() dal quale ci si aspetta che sollevi l'eccezione IllegalStateException()
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato il PlanRequest
     * Alla fine ci aspettiamo che lo status della risposta sia 400 BadRequest
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenString_CreatePlan_ReturnConflict() throws Exception {
        PlanRequest planRequest = new PlanRequest();
        planRequest.setPlanName("");
        given(planeOfferService.createPlan(any())).willThrow(new IllegalStateException());
        String planRequestAsString = new ObjectMapper().writeValueAsString(planRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/planOffer/create/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(planRequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il seguente metodo testa attraverso Mockito l'inserimento di un piano.
     * Il metodo può essere invocato solo dall'admin, quindi per testare ciò basta non settare l'utente che invoca la chiamata
     * così considera l'utente user e il context di SpringSecurity solleva eccezione.
     * Viene mockato un Plan con i parametri corretti e viene successivamente a partire da questo creato
     * un PlanRequest e convertito in stringa
     * Successivamente si chiama il metodo createPlan().
     * Infine, viene eseguita la chiamata post all'indirizzo indicato e viene passato il PlanRequest
     * Alla fine ci aspettiamo che lo status della risposta sia 403 Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenString_CreatePlan_ReturnForbidden() throws Exception {
        Plan plan = Stub.getPlanStub();
        String response = "Piano inserito correttamente";
        PlanRequest planRequest = new PlanRequest();
        planRequest.setPlanName(plan.getName());
        given(planeOfferService.createPlan(any())).willReturn(response);
        String planRequestAsString = new ObjectMapper().writeValueAsString(planRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/planOffer/create/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(planRequestAsString))
                .andExpect(status().isForbidden());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create/offer")
    public ResponseEntity<OfferResponse> createOffer(@Valid @RequestBody OfferRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toOfferResponseByOffer(planeOfferService.createOffer(request)));
        } catch(IllegalStateException ex) {
            return new ResponseEntity<>(OfferResponse.builder().message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Questo test verifica la creazione di un'offerta utilizzando l'annotazione
     * @WithMockUser per simulare un utente con autorità di amministratore.
     * Viene creato un oggetto di tipo Offer utilizzando uno stub predefinito.
     * Successivamente, viene creato un oggetto OfferResponse e impostato il nome dell'offerta.
     * Viene inoltre creato un oggetto OfferRequest a partire dallo stub utilizzando un mapper specifico.
     * Viene mockato il metodo createOffer del servizio planeOfferService per restituire l'oggetto Offer.
     * L'OfferRequest viene convertito in stringa utilizzando ObjectMapper.
     * Viene eseguita una chiamata POST all'endpoint "/api/v1/planOffer/create/offer", includendo la stringa dell'OfferRequest come contenuto JSON.
     * Ci si aspetta che lo status della risposta sia 200 (OK) e che il campo "offer" del corpo della risposta corrisponda al nome dell'offerta impostato in OfferResponse.
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenOfferResponse_CreateOffer_ReturnOk() throws Exception {
        Offer offer = Stub.getOfferStub();
        OfferResponse offerResponse = new OfferResponse();
        offerResponse.setOffer(offer.getName());
        OfferRequest offerRequest = Request.toOfferByOfferRequestMapper(offer);
        given(planeOfferService.createOffer(any())).willReturn(offer);
        String offerRequestAsString = new ObjectMapper().writeValueAsString(offerRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/planOffer/create/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(offerRequestAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offer", is(offerResponse.getOffer())));
    }
    /**
     * Questo test verifica il caso in cui la creazione di un'offerta restituisce una richiesta non valida (Bad Request).
     * Viene creato un oggetto di tipo Offer utilizzando uno stub predefinito.
     * Successivamente, viene creato un oggetto OfferResponse senza impostare il campo dell'offerta ad un valore sbagliato e viene impostato un messaggio di errore.
     * Viene inoltre creato un oggetto OfferRequest a partire dallo stub utilizzando un mapper specifico.
     * Viene mockato il metodo createOffer del servizio planeOfferService per generare un'eccezione IllegalStateException.
     * L'OfferRequest viene convertito in stringa utilizzando ObjectMapper.
     * Viene eseguita una chiamata POST all'endpoint "/api/v1/planOffer/create/offer", includendo la stringa dell'OfferRequest come contenuto JSON.
     * Ci si aspetta che lo status della risposta sia 400 (Bad Request), indicando che la richiesta non è valida.
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenOfferResponse_CreateOffer_ReturnBadRequest() throws Exception {
        Offer offer = Stub.getOfferStub();
        OfferResponse offerResponse = new OfferResponse();
        offerResponse.setOffer(null);
        offerResponse.setMessage("Errore nell'inserimento");
        OfferRequest offerRequest = Request.toOfferByOfferRequestMapper(offer);
        given(planeOfferService.createOffer(any())).willThrow(new IllegalStateException());
        String offerRequestAsString = new ObjectMapper().writeValueAsString(offerRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/planOffer/create/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(offerRequestAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Questo test verifica la creazione di un'offerta utilizzando l'annotazione
     * @WithMockUser che in questo caso possiede i priveleggi di amministratore, il context di Spring Security
     * solleva eccezione in quanto l'utente non è autorizzato.
     * Viene creato un oggetto di tipo Offer utilizzando uno stub predefinito.
     * Successivamente, viene creato un oggetto OfferResponse e impostato il nome dell'offerta.
     * Viene inoltre creato un oggetto OfferRequest a partire dallo stub utilizzando un mapper specifico.
     * Viene mockato il metodo createOffer del servizio planeOfferService per restituire l'oggetto Offer.
     * L'OfferRequest viene convertito in stringa utilizzando ObjectMapper.
     * Viene eseguita una chiamata POST all'endpoint "/api/v1/planOffer/create/offer", includendo la stringa dell'OfferRequest
     * come contenuto JSON. Ci si aspetta che lo status della risposta sia 403 (Forbidden)
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void givenOfferResponse_CreateOffer_ReturnForbidden() throws Exception {
        Offer offer = Stub.getOfferStub();
        OfferResponse offerResponse = new OfferResponse();
        offerResponse.setOffer(offer.getName());
        OfferRequest offerRequest = Request.toOfferByOfferRequestMapper(offer);
        given(planeOfferService.createOffer(any())).willReturn(offer);
        String offerRequestAsString = new ObjectMapper().writeValueAsString(offerRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/planOffer/create/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(offerRequestAsString))
                .andExpect(status().isForbidden());
    }
}
