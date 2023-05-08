package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rifornitore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rifornitoreId;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_localita", nullable = false)
    private Localita localita;

    @OneToMany(mappedBy = "rifornitore")
    private List<Prenotazione> prenotazioni;

    @OneToMany(mappedBy = "rifornitore")
    private List<Sci> sciDisponibili;

    @OneToMany(mappedBy = "rifornitore")
    private List<Snowboard> snowboardDisponibili;

    public Rifornitore(String email){
        this.email = email;
    }

}
