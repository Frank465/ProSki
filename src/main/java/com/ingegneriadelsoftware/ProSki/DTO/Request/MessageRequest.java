package com.ingegneriadelsoftware.ProSki.DTO.Request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class MessageRequest {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String message;
}
