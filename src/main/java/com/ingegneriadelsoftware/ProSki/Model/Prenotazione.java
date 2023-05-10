package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JoinFormula;

import java.time.LocalDate;
import java.util.*;


/**
 *  La prenotazione di sci/snowboard avviene su un rifornitore e riguarda i suoi sci/snowboard
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Prenotazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prenotazioneId;

    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "id_rifornitore")
    private Rifornitore rifornitore;

    @ManyToMany
    @JoinTable(name = "prenotazione_sci", joinColumns =
    @JoinColumn(name = "prenotazione"), inverseJoinColumns =
    @JoinColumn(name = "sci"))
    private List<Sci> sciPrenotati;

    @ManyToMany
    @JoinTable(name = "prenotazione_snowboard", joinColumns =
    @JoinColumn(name = "prenotazione"), inverseJoinColumns =
    @JoinColumn(name = "snowboard"))
    private List<Snowboard> snowboardPrenotati;

    @Column
    private LocalDate dataInizio;

    @Column
    private LocalDate dataFine;
}
