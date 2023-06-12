package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BuySkipass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_cardskipass")
    private CardSkipass cardSkipass;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private LocalDate date;

    public BuySkipass(User user, CardSkipass cardSkipass, Double price, LocalDate date) {
        this.user = user;
        this.cardSkipass = cardSkipass;
        this.price = price;
        this.date = date;
    }
}
