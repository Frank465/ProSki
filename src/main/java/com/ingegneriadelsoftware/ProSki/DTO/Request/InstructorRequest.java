package com.ingegneriadelsoftware.ProSki.DTO.Request;

import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class InstructorRequest {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String surname;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String email;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.SPECIALITA, message = "La specialita può essere solo 'ski' o 'snowboard'")
    private String speciality;

    @NotNull
    @NotEmpty
    private String location;

}
