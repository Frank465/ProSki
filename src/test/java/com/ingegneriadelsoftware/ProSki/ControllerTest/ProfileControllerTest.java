package com.ingegneriadelsoftware.ProSki.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingegneriadelsoftware.ProSki.DTO.Request.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.RegisterRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.AuthenticationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.RegisterResponse;
import com.ingegneriadelsoftware.ProSki.Mapping.Request;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Service.ProfileService;
import com.ingegneriadelsoftware.ProSki.Stub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfileControllerTest {

    private MockMvc mvc;

    private final String BAD_REQUEST = "Bad credentials";

    private final String INTERNAL_ERROR = "Token scaduto, rifare la registrazione";

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ProfileService profileService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     Questo metodo testa la registrazione di un utente, utilizzando JUnit e Mockito..
     Viene creato un oggetto di richiesta di registrazione dell'utente tramite il metodo registerRequestMapper() nella classe Request.
     Successivamente, viene configurato il comportamento del metodo profileService.register() per generare l'utente e il suo token.
     Infine, viene eseguita una richiesta HTTP POST all'indirizzo "/api/v1/profilo/register" contenente l'oggetto di richiesta dell'utente,
     e vengono controllati il codice di stato della risposta (200 OK) e il contenuto della risposta (che contiene i valori di una RegisterResponse: token, messaggio).
     */
    @Test
    public void givenToken_PostRegister_ReturnOK() throws Exception {
        String jwtStub = Stub.getJwtStub_User().substring(7);
        RegisterResponse registerResponse = RegisterResponse.builder().token(jwtStub).build();
        given(profileService.register(any())).willReturn(registerResponse);
        RegisterRequest request = Request.registerRequestMapper(Stub.getUserStub());
        String requestRegisterAsString = new ObjectMapper().writeValueAsString(request);
        System.out.println(registerResponse);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestRegisterAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(jwtStub)))
                .andExpect(jsonPath("$.message", nullValue()));
    }

    /**
     Questo metodo testa la registrazione di un utente che non va a buon fine, utilizzando JUnit e Mockito..
     Viene creato un oggetto di richiesta di registrazione dell'utente tramite il metodo registerRequestMapper() nella classe Request.
     Successivamente, viene configurato il comportamento del metodo profileService.register() per generare l'utente e il suo token.
     Viene settato nellaa request per la registrazione il valore della mail in un formato non valido.
     Infine, viene eseguita una richiesta HTTP POST all'indirizzo "/api/v1/profilo/register" contenente l'oggetto di richiesta dell'utente,
     e vengono controllati il codice di stato della risposta (400 BadRequest)
     */
    @Test
    public void givenToken_PostRegister_ReturnBadRequest() throws Exception {
        String jwtStub = Stub.getJwtStub_User();
        RegisterResponse registerResponse = RegisterResponse.builder().token(jwtStub).build();
        given(profileService.register(any())).willThrow(new IllegalStateException());
        RegisterRequest request = Request.registerRequestMapper(Stub.getUserStub());
        request.setEmail("email_formato@_123errato");
        String requestRegisterAsString = new ObjectMapper().writeValueAsString(request);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestRegisterAsString))
                .andExpect(status().isBadRequest());
    }

    /**
     * Il metodo testa la conferma della registrazione da parte di un utente
     * Viene passato il token dell'utente al metodo confirm(). Dopo questa chiamata l'utente viene definitivamente
     * registrato al sito.
     * Infine, viene eseguita una richiesta HTTP POST all'indirizzo "/api/v1/profilo/confirm"
     * integrandola con il token dell'utente in questione, vengono controllati il codice di stato della risposta (200 OK).
     * @throws Exception
     */
    @Test
    public void givenString_ConfirmRegister_ReturnOk() throws Exception {
        String jwtStub = Stub.getJwtStub_User().substring(7);
        given(profileService.confirmToken(anyString())).willReturn("Confirm");
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/profilo/confirm?token="+jwtStub))
                .andExpect(status().isOk());
    }

    /**
     * Il metodo testa la conferma della registrazione da parte di un utente
     * Viene passato il token dell'utente al metodo confirm(). Viene eseguito il metodo profileService.confirmToken()
     * passando il token dell'utente, il tipo di ritorno in questo caso sarà un IllegalStateException.
     * Infine, viene eseguita una richiesta HTTP POST all'indirizzo "/api/v1/profilo/confirm"
     * integrandola con il token dell'utente in questione, vengono controllati il codice di stato della risposta (500 InternalServer Error).
     * Inoltre viene controllato il messaggio d'errore che ha sollevato l'eccezione
     * L'utente non è stato registrato e dovrà ripetere la registrazione.
     * @throws Exception
     */
    @Test
    public void givenString_ConfirmRegister_ReturnBadRequest() throws Exception {
        String jwtStub = Stub.getJwtStub_User().substring(7);
        given(profileService.confirmToken(anyString()))
                .willThrow(new IllegalStateException(INTERNAL_ERROR));
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/profilo/confirm?token="+jwtStub))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Token scaduto, rifare la registrazione"));
    }

    /**
     * Il metodo testa il comportamento dell'autenticazione dell'utente tramite l'utilizzo di Junit e Mockito.
     * Viene creato un oggetto di richiesta di autenticazione, quindi composto da email e password (AuthenticationRequest)
     * questo valore viene mappato come stringa ed è il valore che prende in input l'endpoint.
     * Successivamente viene simulato il comportamento del profileService.authentication() che ritorn aun Authenticationresponse(DTO)
     * all'interno del quale è presente il token attribuito a quell'utente ed un eventuale messaggio.
     * Infine, viene eseguita una richiesta Http all'indirizzo "/api/v1/profilo/authentication" e ci aspettiamo che tutto
     * vada bene e che lo status della chiamata sia Ok(200) e che sia presente il token corretto nella response
     * @throws Exception
     */
    @Test
    public void givenAuthenticationResponse_postAuthentication_ReturnOK() throws Exception {
        User userStub = Stub.getUserStub();
        String jwtStub = Stub.getJwtStub_User().substring(7);
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().token(jwtStub).build();
        AuthenticationRequest authenticationRequest = Request.authenticationRequestMapper(userStub);
        given(profileService.authentication(any())).willReturn(authenticationResponse);
        String requestUserAuthenticationAsString = new ObjectMapper().writeValueAsString(authenticationRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestUserAuthenticationAsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(jwtStub)))
                .andExpect(jsonPath("$.message", nullValue()));
    }

    /**
     * Il metodo testa il comportamento dell'autenticazione dell'utente quando si mettono valori errati tramite l'utilizzo di Junit e Mockito.
     * Viene creato un oggetto di richiesta di autenticazione con una mail sbagliata e una password (AuthenticationRequest)
     * questo valore viene mappato come stringa ed è il valore che prende in input l'endpoint.
     * Successivamente viene simulato il comportamento del profileService.authentication() che ritorna un Authenticationresponse(DTO)
     * all'interno del quale è presente solo il messaggio d'errore ma nessun token per quell'utente
     * Infine, viene eseguita una richiesta Http all'indirizzo "/api/v1/profilo/authentication" e ci aspettiamo che
     * il test dia errore come status della chimata BAD_REQUEST(400) ed un messagio d'errore.
     * @throws Exception
     */
    @Test
    public void givenAuthenticationResponse_postAuthentication_ReturnBadRequest() throws Exception {
        User userStub = Stub.getUserStub();
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().message(BAD_REQUEST).build();
        doThrow(new IllegalStateException()).when(profileService).authentication(any());
        AuthenticationRequest authenticationRequest = Request.authenticationRequestMapper(userStub);
        authenticationRequest.setEmail("MAIL SBAGLIATA");
        String requestUserAuthenticationAsString = new ObjectMapper().writeValueAsString(authenticationRequest);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUserAuthenticationAsString))
                .andExpect(status().isBadRequest());
    }
}
