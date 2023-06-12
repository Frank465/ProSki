package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.OfferRequest;
import com.ingegneriadelsoftware.ProSki.Email.CreatorEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Email.OfferCreatorEmail;
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
 * La classe contiene tutti i metodi necessari per gestire le offerte
 */
@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final PlanService planService;
    private final EmailSender emailSender;

    public Offer getofferByName(String offer) {
        return offerRepository.findByName(offer).orElseThrow(()-> new IllegalStateException("L'"));
    }


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
        for(Offer offer : offerte) {
            if(offer.getDate().isBefore(LocalDate.now()))
                offerRepository.delete(offer);
        }
        //Controllo esistenza del piano per inserimento offerta
        Plan plan = planService.getPlanByName(request.getPlan());
        //Controllo data
        LocalDate data = Utils.formatterData(request.getDate());
        if(data.isBefore(LocalDate.now())) throw new IllegalStateException("La data inserita non è valida");
        //Controllo esistenza offerta per il piano
        List<Offer> offerPlan = plan.getOffer();
        offerPlan.forEach(cur -> {
            if(request.getName().equalsIgnoreCase(cur.getName()))
                throw new IllegalStateException("L'offerta " + request.getName() + " è già presente per il piano " + cur.getName());
        });
        //Creazione offerta
        Offer newOffer = new Offer(request.getName(), data, request.getDiscount(), plan);
        //Invio email della notifica offerta a tutti gli utenti iscritti al piano
        List<User> utentiPiano = plan.getUsers();
        utentiPiano.forEach( cur -> {
            CreatorEmail email = new OfferCreatorEmail(cur.getUsername(), request.getDate());
            emailSender.send(cur.getEmail(), email.render());
        });
        offerRepository.save(newOffer);
        return newOffer;
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
}
