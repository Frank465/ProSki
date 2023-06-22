package com.ingegneriadelsoftware.ProSki.DTO;

import com.ingegneriadelsoftware.ProSki.DTO.Response.*;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.Model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Il DTOManager fa il mapping dove necessario tra le Entity ed oggetti con determinati attributi che bisogna ritornare al client
 */
@Component
@RequiredArgsConstructor
public class DTOManager {

    public static ReservationResponse toReservationResponseByReservation(Reservation reservation) {
        return ReservationResponse
                .builder()
                .userName(reservation.getUser().getName())
                .vendorName(reservation.getVendor().getName())
                .reservationDate(reservation.getStartDate().toString())
                .filingDate(reservation.getEndDate().toString())
                .snowboardsList(reservation.getSnowboardReserved().toString())
                .skyList(reservation.getSkiReserved().toString())
                .build();
    }

    public static LessonResponse toLessonResponseByLesson(Lesson lesson) {
        return LessonResponse
                .builder()
                .idLesson(lesson.getId())
                .instructor(lesson.getInstructor().getName())
                .startLesson(lesson.getStartLesson().toString())
                .endLesson(lesson.getEndLesson().toString())
                .build();
    }

    public static OfferResponse toOfferResponseByOffer(Offer offer) {
        return OfferResponse
                .builder()
                .offer(offer.getName())
                .date(offer.getDate().toString())
                .plan(offer.getPlan().getName())
                .build();
    }

    /**
     * Mapping tra utenti e UtenteResponse, data una lista di utenti per ognuno si effettua il wrapper con il DTO
     * per tornare una lista di UtentiResponse con solo determinati attributi al controller
     * @param utenti
     * @return
     */
    public static List<UserResponse> toUsersResponseByUsers(List<User> utenti) {
        List<UserResponse> listUsers = new ArrayList<>();
        utenti.forEach( cur -> listUsers.add(
                UserResponse.builder()
                        .userId(cur.getUserId())
                        .name(cur.getName())
                        .surname(cur.getSurname())
                        .email(cur.getEmail())
                        .gender(cur.getGender())
                        //Ricavo l'eta dalla data di nascita
                        .age(Period.between(cur.getDateBirth(), LocalDate.now()).getYears())
                        .plan((cur.getPlan()!= null) ? cur.getPlan().getName() : "")
                        .build())
        );
        return listUsers;
    }

    public static BuySkipassResponse toBuySkipassResponseByBuySkipass(BuySkipass buySkipass) {
        return BuySkipassResponse
                .builder()
                .user(buySkipass.getUser().getName())
                .card(buySkipass.getCardSkipass().getCardCode())
                .price(buySkipass.getPrice().toString())
                .date(buySkipass.getDate().toString())
                .build();
    }

    public static LocationResponse toLocationResponseByLocation(Location location) {
        return LocationResponse.builder()
                    .locationId(location.getLocationId())
                    .name(location.getName())
                    .priceSubscription(location.getPriceSubscription())
                    .startOfSeason(location.getStartOfSeason())
                    .endOfSeason(location.getEndOfSeason())
                    .openingSkiLift(location.getOpeningSkiLift())
                    .closingSkiLift(location.getClosingSkiLift())
                    .build();
    }

}
