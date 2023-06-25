package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.OfferRequest;
import com.ingegneriadelsoftware.ProSki.Email.FactoryMethod.CreatorEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Email.FactoryMethod.OfferCreatorEmail;
import com.ingegneriadelsoftware.ProSki.Model.Offer;
import com.ingegneriadelsoftware.ProSki.Model.Plan;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Repository.OfferRepository;
import com.ingegneriadelsoftware.ProSki.Repository.PlanRepository;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * La classe contiene tutti i metodi necessari per gestire i piani e le offerte
 */
@Service
@RequiredArgsConstructor
public class PlaneOfferService {

    private final OfferRepository offerRepository;
    private final EmailSender emailSender;
    private final PlanRepository planRepository;

    /**
     * Il metodo elimina le offerte scadute, fa il controllo dei dati inseriti se sono validi e crea delle offerte associate ad un
     * piano, alla fine avviene l'inoltro delle mail che notifica l'offerta a tutti gli utenti che sono iscritti al piano indicato
     * @param request
     * @return
     */
    @Transactional
    public Offer createOffer(OfferRequest request) {
        //Controllo ed eventuale eliminazione delle offerte scadute
        Iterable<Offer> offerte = offerRepository.findAll();
        for (Offer offer : offerte) {
            if (offer.getDate().isBefore(LocalDate.now()))
                offerRepository.delete(offer);
        }
        //Controllo esistenza del piano per inserimento offerta
        Plan plan = getPlanByName(request.getPlan());
        //Controllo data
        LocalDate data = Utils.formatterData(request.getDate());
        if (data.isBefore(LocalDate.now())) throw new IllegalStateException("La data inserita non è valida");
        //Controllo esistenza offerta per il piano
        List<Offer> offerPlan = plan.getOffer();
        offerPlan.forEach(cur -> {
            if (request.getName().equalsIgnoreCase(cur.getName()))
                throw new IllegalStateException("L'offerta " + request.getName() + " è già presente per il piano " + cur.getPlan().getName());
        });
        //Invio email della notifica offerta a tutti gli utenti iscritti al piano
        List<User> utentiPiano = plan.getUsers();
        utentiPiano.forEach( cur -> {
            CreatorEmail email = new OfferCreatorEmail(cur.getUsername(), request.getDate());
            emailSender.send(cur.getEmail(), email.render());
        });
        //Controllo se offerta esiste e la associo al piano, altrimenti la creo
        Optional<Offer> offerExist = offerRepository.findByName(String.valueOf(request.getName()).toLowerCase());
        if (offerExist.isEmpty()) {
            Offer newOffer = new Offer(String.valueOf(request.getName()).toLowerCase(), data, request.getDiscount(), plan);
            offerRepository.save(newOffer);
            return newOffer;
        }
        else {
            offerRepository.save(offerExist.get());
            return offerExist.get();
        }
    }

    /**
     * Controllo se per il giorno in cui si vuole fare l'acquisto esista un'offerta
     * @param offer
     * @return
     */
    public Integer getSconto(List<Offer> offer, LocalDate dateReservation) {
        Integer discount = 0;
        for(Offer elem : offer) {
            if (elem.getDate().isEqual(dateReservation))
                discount += elem.getDiscount();
        }
        return discount >= 100 ? 100 : discount;
    }

    /**
     * Il metodo crea il piano controllando che non esista già uno identico
     * @param name
     * @return
     */
    public String createPlan(String name) {
        Optional<Plan> piano = planRepository.findByName(name);
        if(piano.isPresent()) throw new IllegalStateException("Il piano è già presente");
        planRepository.save(new Plan(name));
        return "Il piano " + name + " è stato generato correttamente";
    }

    public Plan getPlanByName(String nomePiano) {
        return planRepository.findByName(nomePiano)
                .orElseThrow(()-> new IllegalStateException("Il piano selezionato non è stato trovato"));
    }
}
