package com.ingegneriadelsoftware.ProSki.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponse {
    private Integer locationId;
    private String name;
    private Double priceSubscription;
    private LocalDate endOfSeason;
    private LocalDate startOfSeason;
    private LocalTime openingSkiLift;
    private LocalTime closingSkiLift;
    private String message;
}
