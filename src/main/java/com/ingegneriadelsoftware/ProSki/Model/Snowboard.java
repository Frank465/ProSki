package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Snowboard extends Reservation {

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

    @Override
    public String toString() {
        return "Snowboards{id='" + id +"}";
    }

}
