package com.ingegneriadelsoftware.ProSki.Model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @SequenceGenerator(
            name = "token_sequence",
            sequenceName = "token_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "token_sequence"
    )
    private Integer id;
    @Column(nullable = false)
    private String tokenName;
    @Column(nullable = false)
    private LocalDateTime createAt;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;
    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "utente_id"
    )
    private Utente utente;

    public Token(String token, LocalDateTime createAt, LocalDateTime expiresAt, Utente utente) {
        this.tokenName = token;
        this.createAt = createAt;
        this.expiresAt = expiresAt;
        this.utente = utente;
    }
}
