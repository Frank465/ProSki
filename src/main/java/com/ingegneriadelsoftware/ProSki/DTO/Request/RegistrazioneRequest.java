package com.ingegneriadelsoftware.ProSki.DTO.Request;


import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


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
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String email;

}
