package com.ingegneriadelsoftware.ProSki.DTO.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class CardSkipassRequest {

    @NotEmpty
    @NotNull
    private String cardCode;

    @NotEmpty
    @NotNull
    private String location;

}
