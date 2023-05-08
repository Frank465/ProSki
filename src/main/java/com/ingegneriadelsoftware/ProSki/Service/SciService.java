package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
import com.ingegneriadelsoftware.ProSki.Model.Sci;
import com.ingegneriadelsoftware.ProSki.Repository.SciRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SciService {
    private final SciRepository sciRepository;

    /**
     * Il metodo data una lista di sci ed un valore, aggiorna per ogni sci il corrispondente valore di enable
     * @param prenotaSci
     */
    public void setEnableSci(List<Sci> prenotaSci, boolean value) {
        prenotaSci.forEach(sci -> sciRepository.setEnableById(sci.getId(), value));
    }

    public Set<Sci> getSciByRifornitore(Integer idRifornitore){
        return sciRepository.findByIdRifornitore(idRifornitore);
    }

    /**
     * Per ogni sci viene settato il rifornitore e salvato nel DB lo sci
     * @param sci
     */
    public void insertSci(List<Sci> sci, Rifornitore rif) {
        sci.forEach(cur -> {cur.setRifornitore(rif); sciRepository.save(cur);});
    }
}
