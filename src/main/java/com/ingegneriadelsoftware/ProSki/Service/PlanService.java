package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.UserPlanRequest;
import com.ingegneriadelsoftware.ProSki.Model.Plan;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Repository.PlanRepository;
import com.ingegneriadelsoftware.ProSki.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    public String createPlan(String nome) {
        Optional<Plan> piano = planRepository.findByName(nome);
        if(piano.isPresent()) throw new IllegalStateException("Il piano è già presente");
        planRepository.save(new Plan(nome));
        return "Il piano " + nome + " è stato generato correttamente";
    }

    public Plan getPlanByName(String nomePiano) {
        return planRepository.findByName(nomePiano)
                .orElseThrow(()-> new IllegalStateException("Il piano selezionato non è stato trovato"));
    }
}
