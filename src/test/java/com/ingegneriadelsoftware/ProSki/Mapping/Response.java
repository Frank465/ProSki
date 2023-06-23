package com.ingegneriadelsoftware.ProSki.Mapping;

import com.ingegneriadelsoftware.ProSki.DTO.Response.ReservationResponse;
import com.ingegneriadelsoftware.ProSki.Model.Reservation;
import com.ingegneriadelsoftware.ProSki.Stub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Response {

    public static ReservationResponse toReservationResponseByReservationMapper(Reservation reservation) {
        return ReservationResponse
                .builder()
                .userName(reservation.getUser().getName())
                .vendorName(reservation.getVendor().getName())
                .reservationDate(reservation.getStartDate().toString())
                .filingDate(reservation.getEndDate().toString())
                .skyList(reservation.getSkiReserved().toString())
                .snowboardsList(reservation.getSnowboardReserved().toString())
                .build();
    }
}
