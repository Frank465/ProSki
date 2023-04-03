package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rifornitore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rifornitoreId;
    @Column(nullable = false)
    private String nome;
    @ManyToOne
    @JoinColumn(name = "id_localita", nullable = false)
    private Localita localita;
    @OneToMany(mappedBy = "rifornitore")
    private List<Prenotazione> prenotazioni;
    @OneToMany(mappedBy = "rifornitore")
    private List<Sci> sciDisponibili;
    @OneToMany(mappedBy = "rifornitore")
    private List<Snowboard> snowboardDisponibili;

    @Override
    public String toString() {
        return "Rifornitore{" +
                "rifornitoreId=" + rifornitoreId +
                ", nome='" + nome + '\'' +
                ", localita=" + localita +
                ", prenotazioni=" + prenotazioni +
                ", sciDisponibili=" + sciDisponibili +
                ", snowboardDisponibili=" + snowboardDisponibili +
                '}';
    }
}
