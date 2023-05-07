package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Model.Sci;
import com.ingegneriadelsoftware.ProSki.Repository.SciRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SciService {
    private final SciRepository sciRepository;

    /**
     * Il metodo per ogni sci nella lista che deve essere prenotato aggiorna il DB con enable a false
     * questi sci non saranno dispoibili fino alla scadenza della prenotazione
     * @param prenotaSci
     */
    public void prenotaSci(List<Sci> prenotaSci) {
        prenotaSci.forEach(sci -> sciRepository.setEnableById(sci.getId(), false));
    }
}
