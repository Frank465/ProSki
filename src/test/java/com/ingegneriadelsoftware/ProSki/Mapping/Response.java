package com.ingegneriadelsoftware.ProSki.Mapping;

import com.ingegneriadelsoftware.ProSki.DTO.Response.LocationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.ReservationResponse;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    public static List<LocationResponse> toLocationResponseListByLocationList(List<Location> locationList) {
        List<LocationResponse> locationResponses = new ArrayList<>();
        locationList.forEach(cur -> {
            locationResponses.add(LocationResponse.builder()
                            .locationId(cur.getLocationId())
                            .name(cur.getName())
                            .priceSubscription(cur.getPriceSubscription())
                            .closingSkiLift(cur.getClosingSkiLift())
                            .startOfSeason(cur.getStartOfSeason())
                            .endOfSeason(cur.getEndOfSeason())
                            .openingSkiLift(cur.getOpeningSkiLift())
                            .build());
        });
        return locationResponses;
    }
}
