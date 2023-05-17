package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Lezione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LocalDateTime inizioLezione;

    @Column
    private LocalDateTime fineLezione;

    @ManyToMany(mappedBy = "lezioniUtente")
    private List<Utente> utenti;

    @ManyToOne
    @JoinColumn(name = "id_maestro")
    private Maestro maestro;

    public Lezione(Maestro maestro, LocalDateTime inizioLezione, LocalDateTime fineLezione) {
        this.inizioLezione = inizioLezione;
        this.fineLezione = fineLezione;
        this.maestro = maestro;
    }
}
