package com.ingegneriadelsoftware.ProSki.Service;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class ValidazioneEmail implements Predicate<String> {

    @Override
    public boolean test(String o) {
        return true;
    }
}
