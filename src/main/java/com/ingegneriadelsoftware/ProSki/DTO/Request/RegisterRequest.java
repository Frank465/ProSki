package com.ingegneriadelsoftware.ProSki.DTO.Request;


import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class  RegisterRequest {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String surname;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String email;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.SESSO, message = "Il campo sesso deve contenere la parola uomo oppure donna")
    private String gender;

    @NotNull
    @NotEmpty
    @DateTimeFormat(pattern="dd/MM/yyyy")
    private String dateBirth;

}
