package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.InstructorRequest;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Model.Instructor;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstructorService {
    private final InstructorRepository instructorRepository;
    private final LocationService locationService;

    public Instructor getInstructorByEmail(String email) throws EntityNotFoundException {
        return instructorRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Il maestro non esiste"));
    }

    /**
     * Creazione di un maestro e attribuzione ad una località esistente
     * @param request
     * @return
     * @throws IllegalStateException
     */
    public String insertInstructor(InstructorRequest request) throws EntityNotFoundException{
        Optional <Instructor> maestro = instructorRepository.findByEmail(request.getEmail());
        if(maestro.isPresent()) throw new IllegalStateException("Il maestro è già stato inserito");
        Location location = locationService.getLocalitaByName(request.getLocation());
        Instructor newInstructor = new Instructor(request.getName(), request.getSurname(), request.getEmail(), request.getSpeciality(), location);
        instructorRepository.save(newInstructor);
        return "maestro "+ newInstructor.getName()+" aggiunto con successo";
    }
}
