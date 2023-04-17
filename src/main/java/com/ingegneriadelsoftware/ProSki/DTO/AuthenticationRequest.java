package com.ingegneriadelsoftware.ProSki.DTO;

import lombok.*;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class AuthenticationRequest {
    private String email;
    private String password;
}
