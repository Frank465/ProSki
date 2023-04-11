package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

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
    @OneToMany(mappedBy = "prenotazione")
    private List<Sci> sciPrenotati;
    @OneToMany(mappedBy = "prenotazione")
    private List<Snowboard> snowboardprenotati;
    private Date data;
    private Date ora;
}
