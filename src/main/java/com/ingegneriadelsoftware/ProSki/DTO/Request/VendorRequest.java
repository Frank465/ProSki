package com.ingegneriadelsoftware.ProSki.DTO.Request;

import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class VendorRequest {
    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String email;

    @NotNull
    @NotEmpty
    private String location;
}
