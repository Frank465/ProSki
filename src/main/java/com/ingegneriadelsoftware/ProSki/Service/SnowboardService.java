package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Repository.SnowboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SnowboardService {

    private final SnowboardRepository snowboardRepository;

    /**
     * Il metodo data una lista di snowboards ed un valore, aggiorna per ogni snowboard il corrispondente valore di enable
     * @param prenotaSnowboards value
     */
    public void setEnableSnowboards(List<Snowboard> prenotaSnowboards, boolean value) {
        prenotaSnowboards.forEach(snowboard -> snowboardRepository.setEnableById(snowboard.getId(), value));
    }

    public Set<Snowboard> getSnowboradByRifornitore(Integer idRifornitore){
        return snowboardRepository.findByIdRifornitore(idRifornitore);
    }

    /**
     * Per ogni elemento viene settato il rifornitore e salvato nel DB
     * @param snowboards
     * @param rifornitore
     */
    public void insertSnowboard(List<Snowboard> snowboards, Rifornitore rifornitore) {
        snowboards.forEach(cur -> {cur.setRifornitore(rifornitore); snowboardRepository.save(cur);});
    }
}
