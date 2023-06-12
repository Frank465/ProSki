package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOffer;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer discount;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    private Plan plan;

    public Offer(String name, LocalDate date, Integer discount, Plan plan) {
        this.name = name;
        this.date = date;
        this.discount = discount;
        this.plan = plan;
    }


}

