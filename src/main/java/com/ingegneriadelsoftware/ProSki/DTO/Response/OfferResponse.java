package com.ingegneriadelsoftware.ProSki.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferResponse {
    private String offer;
    private String plan;
    private String date;
    private String message;
}
