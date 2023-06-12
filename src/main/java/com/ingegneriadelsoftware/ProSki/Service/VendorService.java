package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorEquipmentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.SkiDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.SnowboardDTO;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Model.Vendor;
import com.ingegneriadelsoftware.ProSki.Model.Ski;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Repository.VendorRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SkiRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SnowboardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final LocationService locationService;
    private final SkiRepository skiRepository;
    private final SnowboardRepository snowboardRepository;

    /**
     * Il metodo crea un rifornitore
     * @param request
     * @return String
     * @throws IllegalStateException
     * @throws EntityNotFoundException
     */
    public String insertVendor(VendorRequest request) throws EntityNotFoundException {
        //Controllo se il rifornitore è gia presente
        if(vendorRepository.findByEmail(request.getEmail()).isPresent()) throw new IllegalStateException("Rifornitore già presente");
        //Controllo se la localita del fornitore esiste
        Location location = locationService.getLocalitaByName(request.getLocation());
        Vendor newVendor = new Vendor(request.getName(), request.getEmail(), location);
        vendorRepository.save(newVendor);
        return "Rifornitore " + request.getName() + " è stato inserito correttamente";
    }

    /**
     * Il metodo ritorna le attrezzature tra sci e snowboards di un rifornitore che sono attualmente disponibili
     * @param IdRifornitore
     * @return
     */
    public EquipmentAvailableResponse getEquipmentAvailable(Integer idVendor) {
        Vendor vendor = getRifornitoreById(idVendor);
        List<Ski> ski = skiRepository.findByVendor(vendor);
        List<Snowboard> snowboards = snowboardRepository.findByVendor(vendor);

        List<Snowboard> snowboardsAvaiable = snowboards.stream()
                .filter(cur -> cur.isEnable())  // filtra solo gli snowboards che non sono stati prenotati
                .toList(); //Inserisci gli elementi nella lista

        List<Ski> skiAvaiable = ski.stream()
                .filter(cur -> cur.isEnable())  // filtra solo gli sci che non sono stati prenotati
                .toList(); //Inserisci gli elementi nella lista

        //Creo una lista di snowboard dto con i parametri da tornare al client
        List<SnowboardDTO> snowboardsDTO = new ArrayList<>();
        snowboardsAvaiable.forEach(cur -> snowboardsDTO.add(SnowboardDTO.builder()
                .id(cur.getId())
                .measure(cur.getMeasure()).build()));

        //Creo una lista di sci dto con i parametri da tornare al client
        List<SkiDTO> skisDTO = new ArrayList<>();
        skiAvaiable.forEach(cur -> skisDTO.add(SkiDTO.builder()
                .id(cur.getId())
                .measure(cur.getMeasure()).build()));

        return EquipmentAvailableResponse
                .builder()
                .vendorEmail(vendor.getEmail())
                .snowboardsList(snowboardsDTO)
                .skisList(skisDTO)
                .build();
    }

    /**
     * Cerca rifornitore tramite email
     * @param email
     * @return
     * @throws IllegalStateException
     */
    public Vendor getVendorByEmail(String email) throws IllegalStateException {
        return vendorRepository.findByEmail(email).
                orElseThrow(()-> new IllegalStateException("Il rifornitore non è stato trovato"));
    }

    /**
     * Cerca fornitore tramite ID
     * @param idRifornitore
     * @return
     * @throws IllegalStateException
     */
    public Vendor getRifornitoreById(Integer id) throws IllegalStateException {
        return vendorRepository.findById(id).
                orElseThrow(()-> new IllegalStateException("Il rifornitore non è stato trovato"));
    }

    /**
     * Le attrezzature che arrivano dalla request vengono inserite nell'inventario di un rifornitore
     * @param request
     * @return
     */
    public String createEquipment(VendorEquipmentRequest request) {
        Vendor vendor = getVendorByEmail(request.getVendorEmail());
        insertSky(request.getSki(), vendor);
        insertSnowboards(request.getSnowboards(), vendor);
        return "Attrezzature inserite correttamente";
    }

    /**
     * Per ogni sci viene settato il rifornitore e salvato nel DB lo sci
     * @param ski
     */
    private void insertSky(List<Ski> ski, Vendor rif) {
        ski.forEach(cur -> {cur.setVendor(rif);
            skiRepository.save(cur);});
    }

    /**
     * Per ogni elemento viene settato il rifornitore e salvato nel DB
     * @param snowboards
     * @param vendor
     */
    private void insertSnowboards(List<Snowboard> snowboards, Vendor vendor) {
        snowboards.forEach(cur -> {cur.setVendor(vendor); snowboardRepository.save(cur);});
    }
}
