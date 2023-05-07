package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Repository.SnowboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SnowboardService {

    private final SnowboardRepository snowboardRepository;

    /**
     * Il metodo per ogni snowboard nella lista che deve essere prenotato aggiorna il DB con enable a false
     * questi snowboard non saranno disponibili fino alla scadenza della prenotazione
     * @param prenotaSnowboards
     */
    public void prenotaSnowboards(List<Snowboard> prenotaSnowboards) {
        prenotaSnowboards.forEach(snowboard -> snowboardRepository.setEnableById(snowboard.getId(), false));
    }
}
