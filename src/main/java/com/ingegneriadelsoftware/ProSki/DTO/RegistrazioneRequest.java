package com.ingegneriadelsoftware.ProSki.DTO;


import lombok.*;


@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrazioneRequest {
    private String nome;
    private String cognome;
    private String password;
    private String email;

}
