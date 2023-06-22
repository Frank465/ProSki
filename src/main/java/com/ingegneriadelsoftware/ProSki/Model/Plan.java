package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer planId;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "plan")
    List<User> users;

    @OneToMany(mappedBy = "plan")
    private List<Offer> offer;

    public Plan(String name) {
        this.name = name;
    }

}
