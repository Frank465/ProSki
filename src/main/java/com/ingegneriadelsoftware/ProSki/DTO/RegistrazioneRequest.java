package com.ingegneriadelsoftware.ProSki.DTO;


import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrazioneRequest {

    @NotNull
    @NotEmpty
    private String nome;

    @NotNull
    @NotEmpty
    private String cognome;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL)
    private String email;

}
