package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Snowboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer misura;

    @Column
    private boolean enable = true;

    @ManyToOne
    @JoinColumn(name = "id_rifornitore", nullable = false)
    private Rifornitore rifornitore;

    @ManyToMany(mappedBy = "snowboardPrenotati")
    private List<Prenotazione> prenotazione;

    @Override
    public String toString() {
        return "Snowboards{id='" + id +"}";
    }

}
