package com.ingegneriadelsoftware.ProSki.DTO.Request;

import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class OfferRequest {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Integer discount;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATE, message = Utils.ERROR_LOCALDATE)
    private String date;

    @NotNull
    @NotEmpty
    private String plan;

}
