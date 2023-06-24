package com.ingegneriadelsoftware.ProSki.Mapping;

import com.ingegneriadelsoftware.ProSki.DTO.Request.*;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Stub;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class Request {

    public static VendorRequest toVendorRequestByVendorMapper(Vendor vendor) {
        VendorRequest vendorRequest = new VendorRequest();
        vendorRequest.setEmail(vendor.getEmail());
        vendorRequest.setLocation(vendor.getLocation().getName());
        vendorRequest.setName(vendor.getName());
        return vendorRequest;
    }

    public static CardSkipassRequest toCardSkipassrequestByCardSkipassMapper(CardSkipass cardSkipass) {
        CardSkipassRequest cardSkipassRequest = new CardSkipassRequest();
        cardSkipassRequest.setCardCode(cardSkipass.getCardCode());
        cardSkipassRequest.setLocation(cardSkipass.getLocation().getName());
        return cardSkipassRequest;
    }

    public static OfferRequest toOfferByOfferRequestMapper(Offer offer) {
        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setName(offer.getName());
        offerRequest.setPlan(offer.getPlan().getName());
        offerRequest.setDiscount(offer.getDiscount());
        offerRequest.setDate("12/12/2024");
        return offerRequest;
    }

    public static InstructorRequest instructorRequestMapper(Instructor instructor) {
        InstructorRequest instructorRequest = new InstructorRequest();
        instructorRequest.setEmail(instructor.getEmail());
        instructorRequest.setSpeciality(instructor.getSpeciality());
        instructorRequest.setLocation(instructor.getLocation().getName());
        instructorRequest.setName(instructor.getName());
        instructorRequest.setSurname(instructor.getSurname());
        return instructorRequest;
    }

    public static RegisterRequest registerRequestMapper(User user) {
        RegisterRequest request = new RegisterRequest();
        request.setName(user.getName());
        request.setSurname(user.getSurname());
        request.setEmail(user.getEmail());
        request.setGender(user.getGender().toString());
        request.setPassword(user.getPassword());
        request.setDateBirth(user.getDateBirth().toString());
        return request;
    }

    public static AuthenticationRequest authenticationRequestMapper(User user) {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail(user.getEmail());
        request.setPassword(user.getPassword());
        return request;
    }

    public static BuySkipassRequest buySkipassRequestMapper(BuySkipass buySkipass) {
        BuySkipassRequest buySkipassRequest = new BuySkipassRequest();
        buySkipassRequest.setDate("12/12/2024");
        buySkipassRequest.setCardCode(buySkipass.getCardSkipass().getCardCode());
        return buySkipassRequest;
    }

    public static ReservationRequest reservationRequestMapper(Reservation reservation) {
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setVendorEmail("vendor1@gmail.com");
        reservationRequest.setStartDate("19/04/2024");
        reservationRequest.setEndDate("19/04/2024");
        reservationRequest.setSkisList(Stub.getSkiDTOListStub());
        reservationRequest.setSnowboardsList(Stub.getSnowboardDTOListStub());
        return reservationRequest;
    }
}
