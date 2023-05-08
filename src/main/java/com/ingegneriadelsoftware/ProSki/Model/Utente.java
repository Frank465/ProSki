package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Utente implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "utente_sequence",
            sequenceName = "utente_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "utente_sequence"
    )
    private Integer utenteId;
    @Column
    private String nome;
    @Column
    private String cognome;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @OneToMany(mappedBy = "utente")
    private List<Prenotazione> prenotazioni;
    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;
    private boolean locked = false;
    private boolean enable = false;
    @Column
    private String token;


    public Utente(String email){
        this.email = email;
    }
    public Utente(String nome, String cognome, String password, String email, Ruolo ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.password = password;
        this.email = email;
        this.ruolo = ruolo;
    }
    public Utente(String password, String email, Ruolo ruolo, boolean enable) {
        this.password = password;
        this.email = email;
        this.ruolo = ruolo;
        this.enable = enable;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ruolo.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
