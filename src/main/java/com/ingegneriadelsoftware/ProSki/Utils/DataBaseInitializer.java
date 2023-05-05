package com.ingegneriadelsoftware.ProSki.Utils;

import com.ingegneriadelsoftware.ProSki.Model.Localita;
import com.ingegneriadelsoftware.ProSki.Model.Maestro;
import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
import com.ingegneriadelsoftware.ProSki.Repository.LocalitaRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Il metodo all'avvio dell'applicazione popola in automatico il db con localita, rifornitori e maestri
 */
@AllArgsConstructor
@Component
public class DataBaseInitializer implements CommandLineRunner {
    private final LocalitaRepository localitaRepository;

    @Override
    public void run(String... args) throws Exception {


    }
}
