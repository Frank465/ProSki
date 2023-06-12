package com.ingegneriadelsoftware.ProSki.DTO.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponse {
    private String userName;
    private String vendorName;
    private String reservationDate;
    private String filingDate;
    private String skyList;
    private String snowboardsList;
}
