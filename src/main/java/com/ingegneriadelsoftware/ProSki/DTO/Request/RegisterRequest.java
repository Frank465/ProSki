package com.ingegneriadelsoftware.ProSki.DTO.Request;


import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;


@Data
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
    @Pattern(regexp = Utils.REGEX_PASSWORD, message = Utils.ERROR_PASSWORD)
    private String password;

    @NotNull
    @NotEmpty
    @Email(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String email;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.SESSO, message = "Bisogna scrivere man o women")
    private String gender;

    @NotNull
    @NotEmpty
    @DateTimeFormat(pattern="dd/MM/yyyy")
    private String dateBirth;

}
