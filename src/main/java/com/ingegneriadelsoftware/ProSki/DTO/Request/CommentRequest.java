package com.ingegneriadelsoftware.ProSki.DTO.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class CommentRequest {

    @NotNull
    @NotEmpty
    //Email rifornitore/maestro oppure nome località
    private String username;

    @NotNull
    private Integer idMessage;

    @NotNull
    @NotEmpty
    private String comment;

}
