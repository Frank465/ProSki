package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer locationId;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "location")
    private List<Vendor> vendors;

    @OneToMany(mappedBy = "location")
    private List<Instructor> instructors;

    @OneToMany(mappedBy ="location")
    private List<CardSkipass> cardSkipasses;

    @Column
    private Double priceSubscription;

    @Column
    private LocalDate endOfSeason;

    @Column
    private LocalDate startOfSeason;

    @Column
    private LocalTime openingSkiLift;

    @Column
    private LocalTime closingSkiLift;

    public Location(String name, Double priceSubscription, LocalDate endOfSeason, LocalDate startOfSeason,
                    LocalTime openingSkiLift, LocalTime closingSkiLift) {
        this.name = name;
        this.priceSubscription = priceSubscription;
        this.endOfSeason = endOfSeason;
        this.startOfSeason = startOfSeason;
        this.openingSkiLift = openingSkiLift;
        this.closingSkiLift = closingSkiLift;
    }
}
