package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Localita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer localitaId;
    @Column(nullable = false, unique = true)
    private String nome;
    @OneToMany(mappedBy = "localita")
    private List<Rifornitore> rifornitori;
    @OneToMany(mappedBy = "localita")
    private List<Maestro> maestri;
    @OneToMany(mappedBy ="localita")
    private List<Skipass> skipasses;
    private Double prezzoAbbonamento;

}
