package com.ingegneriadelsoftware.ProSki.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuySkipassResponse {
    private String user;
    private String card;
    private String date;
    private String price;
    private String message;

}
