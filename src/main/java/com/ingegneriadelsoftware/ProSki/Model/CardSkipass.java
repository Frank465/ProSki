package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CardSkipass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String cardCode;

    @ManyToOne
    @JoinColumn(name = "id_location")
    private Location location;

    @OneToMany(mappedBy = "cardSkipass")
    private List<BuySkipass> buySkipasses;

    public CardSkipass(String cardCode, Location location) {
        this.cardCode = cardCode;
        this.location = location;
    }
}
