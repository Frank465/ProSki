package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ski  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer measure;

    @ManyToOne
    @JoinColumn(name = "id_vendor")
    private Vendor vendor;

    @ManyToMany(mappedBy = "skiReserved")
    private List<Reservation> reservationSki;

    public Ski(Integer id, Integer measure) {
        this.id = id;
        this.measure = measure;
    }

    @Override
    public String toString() {
        return "Sci{id='" + id + "}";
    }

}
