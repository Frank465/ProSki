package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Response.AttrezzaturaDisponibileResponse;
import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
import com.ingegneriadelsoftware.ProSki.Model.Sci;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Repository.RifornitoreRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SciRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SnowboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RifornitoreService {

    private final RifornitoreRepository rifornitoreRepository;
    private final SciRepository sciRepository;
    private final SnowboardRepository snowboardRepository;


    public String inserisciRifornitore(Rifornitore newRifornitore) {
        Optional<Rifornitore> rifornitore = rifornitoreRepository.findByEmail(newRifornitore.getEmail());
        if(rifornitore.isPresent()) throw new IllegalStateException("Rifornitore già presente");
        rifornitoreRepository.save(newRifornitore);
        return "Rifornitore " + newRifornitore.getNome() + " è stato inserito correttamente";
    }

    /**
     * Il metodo ritorna le attrezzature tra sci e snowboards di un rifornitore che sono attualmente disponibili
     * @param rifornitore
     * @return
     */
    public AttrezzaturaDisponibileResponse getAttrezzaturaDisponibile(Rifornitore rifornitore) {
        Optional<Rifornitore> rif = rifornitoreRepository.findByEmail(rifornitore.getEmail());

        if(rif.isEmpty()) throw new IllegalStateException("Il rifornitore inserito non esiste");

        Set<Sci> sci = sciRepository.findByRifornitoreId(rif.get().getRifornitoreId());
        Set<Snowboard> snowboards = snowboardRepository.findByRifornitoreId(rif.get().getRifornitoreId());

        List<Snowboard> snowboardsDisponibili = snowboards.stream()
                .filter(cur -> !cur.isEnable())  // filtra solo gli snowboards che non sono stati prenotati
                .toList(); //Inserisci gli elementi nella lista

        List<Sci> sciDisponibili = sci.stream()
                .filter(cur -> !cur.isEnable())  // filtra solo gli sci che non sono stati prenotati
                .toList(); //Inserisci gli elementi nella lista

        return AttrezzaturaDisponibileResponse
                .builder()
                .snowboardList(snowboardsDisponibili)
                .sciList(sciDisponibili)
                .build();
    }

}
