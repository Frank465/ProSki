package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "id_rifornitore", nullable = false)
    private Rifornitore rifornitore;
    @ManyToMany
    private List<Sci> sciPrenotati;
    @ManyToMany
    private List<Snowboard> snowboardprenotati;
    @Column
    private LocalDate dataInizio;
    @Column
    private LocalDate dataFine;
}
