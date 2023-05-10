package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sci {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private Integer misura;
    @Column
    private boolean enable = true;
    @ManyToOne
    @JoinColumn(name = "id_rifornitore")
    private Rifornitore rifornitore;
    @ManyToMany(mappedBy = "sciPrenotati")
    private List<Prenotazione> prenotazioneSci;

    @Override
    public String toString() {
        return "Sci{id='" + id + "}";
    }

}
