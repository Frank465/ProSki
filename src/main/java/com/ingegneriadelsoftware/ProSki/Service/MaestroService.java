package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Model.Localita;
import com.ingegneriadelsoftware.ProSki.Model.Maestro;
import com.ingegneriadelsoftware.ProSki.Repository.MaestroRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaestroService {
    private final MaestroRepository maestroRepository;
    private final LocalitaService localitaService;


    public String inserisciMaestro(Maestro maestro, String localita) throws EntityNotFoundException {
        Optional<Maestro> emailMaestro = maestroRepository.findByEmail(maestro.getEmail());

        if(emailMaestro.isPresent()) throw new IllegalStateException("maestro già presente");
        if(localita != null) {
            Localita newLocalita = localitaService.getLocalita(localita);
            maestro.setLocalita(newLocalita);
        }
        maestroRepository.save(maestro);

        return "maestro "+ maestro.getNome()+" aggiunto con successo";
    }
}
