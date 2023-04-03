package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Snowboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double misura;
    @ManyToOne
    @JoinColumn(name = "id_rifornitore", nullable = false)
    private Rifornitore rifornitore;
    @ManyToOne
    @JoinColumn(name = "id_prenotazione")
    private Prenotazione prenotazione;

    @Override
    public String toString() {
        return "Snowboard{" +
                "id=" + id +
                ", misura=" + misura +
                ", rifornitore=" + rifornitore +
                '}';
    }
}
