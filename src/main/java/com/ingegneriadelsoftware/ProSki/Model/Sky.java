package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sky {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer measure;

    @Column
    private boolean enable = true;

    @ManyToOne
    @JoinColumn(name = "id_vendor")
    private Vendor vendor;

    @ManyToMany(mappedBy = "skyReserved")
    private List<Reservation> reservationSky;

    @Override
    public String toString() {
        return "Sci{id='" + id + "}";
    }

}
