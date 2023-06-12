package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorEquipmentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Model.Vendor;
import com.ingegneriadelsoftware.ProSki.Model.Sky;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Repository.VendorRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SkyRepository;
import com.ingegneriadelsoftware.ProSki.Repository.SnowboardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final LocationService locationService;
    private final SkyRepository skyRepository;
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
    public EquipmentAvailableResponse getEquipmentAvailable(Integer idRifornitore) {
        Vendor vendor = getRifornitoreById(idRifornitore);
        List<Sky> sky = skyRepository.findByVendor(vendor);
        List<Snowboard> snowboards = snowboardRepository.findByVendor(vendor);

        List<Snowboard> snowboardsAvaiable = snowboards.stream()
                .filter(cur -> cur.isEnable())  // filtra solo gli snowboards che non sono stati prenotati
                .toList(); //Inserisci gli elementi nella lista

        List<Sky> skyAvaiable = sky.stream()
                .filter(cur -> cur.isEnable())  // filtra solo gli sci che non sono stati prenotati
                .toList(); //Inserisci gli elementi nella lista

        return EquipmentAvailableResponse
                .builder()
                .snowboardList(snowboardsAvaiable.stream().map(cur->cur.getId()).toList())
                .skyList(skyAvaiable.stream().map(cur->cur.getId()).toList())
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
        insertSky(request.getSky(), vendor);
        insertSnowboards(request.getSnowboards(), vendor);
        return "Attrezzature inserite correttamente";
    }

    /**
     * Per ogni sci viene settato il rifornitore e salvato nel DB lo sci
     * @param sky
     */
    private void insertSky(List<Sky> sky, Vendor rif) {
        sky.forEach(cur -> {cur.setVendor(rif);skyRepository.save(cur);});
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
