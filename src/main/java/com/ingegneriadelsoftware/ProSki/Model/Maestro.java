package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Maestro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String nome;

    @Column
    private String cognome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String specialita;

    @ManyToOne
    @JoinColumn(name = "id_localita")
    private Localita localita;

    @OneToMany(mappedBy = "maestro")
    private List<Lezione> lezioneList;

    public Maestro(String email) {
        this.email = email;
    }
}
