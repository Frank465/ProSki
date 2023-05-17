package com.ingegneriadelsoftware.ProSki.DTO.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LezioneResponse {
    private Integer idLezione;
    private String maestro;
    private String inizioLezione;
    private String fineLezione;
}
