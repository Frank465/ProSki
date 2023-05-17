package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.AttrezzatureRifornitoreRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.AttrezzaturaDisponibileResponse;
import com.ingegneriadelsoftware.ProSki.Model.Localita;
import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
import com.ingegneriadelsoftware.ProSki.Model.Sci;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Repository.RifornitoreRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SciRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SnowboardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RifornitoreService {

    private final RifornitoreRepository rifornitoreRepository;
    private final SciService sciService;
    private final LocalitaService localitaService;
    private final SnowboardService snowboardService;
    private final SciRepository sciRepository;
    private final SnowboardRepository snowboardRepository;


    /**
     * Il metodo crea un rifornitore
     * @param newRifornitore
     * @return
     * @throws IllegalStateException
     * @throws EntityNotFoundException
     */
    public String inserisciRifornitore(Rifornitore newRifornitore) throws IllegalStateException, EntityNotFoundException {
        //Controllo se il rifornitore è gia presente
        Optional<Rifornitore> rifornitore = rifornitoreRepository.findByEmail(newRifornitore.getEmail());
        if(rifornitore.isPresent()) throw new IllegalStateException("Rifornitore già presente");
        //Controllo se la localita del fornitore esiste
        Localita localita = localitaService.getLocalita(newRifornitore.getLocalita().getNome());
        newRifornitore.setLocalita(localita);
        rifornitoreRepository.save(newRifornitore);
        return "Rifornitore " + newRifornitore.getNome() + " è stato inserito correttamente";
    }

    /**
     * Il metodo ritorna le attrezzature tra sci e snowboards di un rifornitore che sono attualmente disponibili
     * @param IdRifornitore
     * @return
     */
    public AttrezzaturaDisponibileResponse getAttrezzaturaDisponibile(Integer idRifornitore) {
        Rifornitore rifornitore = getRifornitoreById(idRifornitore);
        List<Sci> sci = sciRepository.findByRifornitore(rifornitore);
        List<Snowboard> snowboards = snowboardRepository.findByRifornitore(rifornitore);

        List<Snowboard> snowboardsDisponibili = snowboards.stream()
                .filter(cur -> cur.isEnable())  // filtra solo gli snowboards che non sono stati prenotati
                .toList(); //Inserisci gli elementi nella lista

        List<Sci> sciDisponibili = sci.stream()
                .filter(cur -> cur.isEnable())  // filtra solo gli sci che non sono stati prenotati
                .toList(); //Inserisci gli elementi nella lista

        return AttrezzaturaDisponibileResponse
                .builder()
                .snowboardList(snowboardsDisponibili.stream().map(cur->cur.getId()).toList())
                .sciList(sciDisponibili.stream().map(cur->cur.getId()).toList())
                .build();
    }

    /**
     * Cerca rifornitore tramite email
     * @param email
     * @return
     * @throws IllegalStateException
     */
    public Rifornitore getRifornitoreByEmail(String email) throws IllegalStateException {
        return rifornitoreRepository.findByEmail(email).
                orElseThrow(()-> new IllegalStateException("Il rifornitore non è stato trovato"));
    }

    /**
     * Cerca fornitore tramite ID
     * @param idRifornitore
     * @return
     * @throws IllegalStateException
     */
    public Rifornitore getRifornitoreById(Integer idRifornitore) throws IllegalStateException {
        return rifornitoreRepository.findById(idRifornitore).
                orElseThrow(()-> new IllegalStateException("Il rifornitore non è stato trovato"));
    }

    /**
     * Le attrezzature che arrivano dalla request vengono inserite nell'inventario di un rifornitore
     * @param request
     * @return
     */
    public String createAttrezzature(AttrezzatureRifornitoreRequest request) {
        Rifornitore rifornitore = getRifornitoreByEmail(request.getEmailRifornitore());
        sciService.insertSci(request.getSci(), rifornitore);
        snowboardService.insertSnowboard(request.getSnowboards(), rifornitore);
        return "Attrezzature inserite correttamente";
    }
}
