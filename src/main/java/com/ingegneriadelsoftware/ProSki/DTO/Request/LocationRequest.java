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
public class LocationRequest {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Double priceSubscription;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATE, message = Utils.ERROR_LOCALDATE)
    private String startOfSeason;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATE, message = Utils.ERROR_LOCALDATE)
    private String endOfSeason;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALTIME, message = Utils.ERROR_LOCALTIME)
    private String openingSkiLift;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALTIME, message = Utils.ERROR_LOCALTIME)
    private String closingSkiLift;

}

