package com.ingegneriadelsoftware.ProSki.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrazioneResponse {

    private String token;
    private String message = null;
}
