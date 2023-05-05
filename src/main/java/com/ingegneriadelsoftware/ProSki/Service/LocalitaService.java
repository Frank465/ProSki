package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Model.Localita;
import com.ingegneriadelsoftware.ProSki.Repository.LocalitaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LocalitaService {
    private final LocalitaRepository localitaRepository;

    public Localita getLocalita(String nomeLocalita) throws EntityNotFoundException {
        return localitaRepository.findByNome(nomeLocalita)
                .orElseThrow(() -> new EntityNotFoundException("Localita non trovata"));
    }

    public String creaLocalita(Localita localita){
        Optional<Localita> loc = localitaRepository.findByNome(localita.getNome());

        if(loc.isPresent()) throw new IllegalStateException("località gia presente");
        localitaRepository.save(localita);

        return "localita creata con successo";
    }



}
