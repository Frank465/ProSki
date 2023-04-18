package com.ingegneriadelsoftware.ProSki.Service;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidazioneEmail implements Predicate<String> {

    private static final String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    //Controllo Email
    @Override
    public boolean test(String email) throws IllegalArgumentException  {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches())
            throw new IllegalArgumentException("Email non valida");
        return true;
    }
}
