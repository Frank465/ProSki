package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Snowboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer measure;

    @ManyToOne
    @JoinColumn(name = "id_vendor", nullable = false)
    private Vendor vendor;

    @ManyToMany(mappedBy = "snowboardReserved")
    private List<Reservation> reservation;

    public Snowboard(Integer id, Integer measure) {
        this.id = id;
        this.measure = measure;
    }

    @Override
    public String toString() {
        return "Snowboards{id='" + id +"}";
    }

}
