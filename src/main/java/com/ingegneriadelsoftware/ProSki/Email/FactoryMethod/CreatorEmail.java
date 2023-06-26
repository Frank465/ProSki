package com.ingegneriadelsoftware.ProSki.Email.FactoryMethod;

import com.ingegneriadelsoftware.ProSki.Email.FactoryMethod.BuildEmail;
import org.springframework.stereotype.Component;


/**
 * Classe astratta che quando viene chiamato il metodo render richiama le classi concrete del tipo runtime
 * facendo il createEmail() apposito e poi viene eseguito il metodo render.
 */
@Component
public abstract class CreatorEmail {

    public abstract BuildEmail createEmail();

    public String render() {
        // Chiama il factory method per creare un oggetto di tipo BuildEmail
        BuildEmail email = createEmail();
        // Ora si usa il BuildEmail.
        return email.render();
    }
}
