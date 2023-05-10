package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
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
     * Il metodo data una lista di sci ed un valore, aggiorna per ogni sci il corrispondente valore di enable
     * @param prenotaSci
     */
    public void setEnableSci(List<Sci> prenotaSci, boolean enable) {
        prenotaSci.forEach(sci -> {
            sciRepository.setEnable(enable, sci.getId());
        });
    }

    /**
     * Per ogni sci viene settato il rifornitore e salvato nel DB lo sci
     * @param sci
     */
    public void insertSci(List<Sci> sci, Rifornitore rif) {
        sci.forEach(cur -> {cur.setRifornitore(rif); sciRepository.save(cur);});
    }
}
