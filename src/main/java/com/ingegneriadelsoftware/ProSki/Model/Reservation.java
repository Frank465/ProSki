package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;


/**
 *  La prenotazione di sci/snowboard avviene su un rifornitore e riguarda i suoi sci/snowboard
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationId;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_vendor")
    private Vendor vendor;

    @ManyToMany
    @JoinTable(name = "reservation_sky", joinColumns =
    @JoinColumn(name = "reservation"), inverseJoinColumns =
    @JoinColumn(name = "sky"))
    private List<Sky> skyReserved;

    @ManyToMany
    @JoinTable(name = "reservation_snowboard", joinColumns =
    @JoinColumn(name = "reservation"), inverseJoinColumns =
    @JoinColumn(name = "snowboard"))
    private List<Snowboard> snowboardReserved;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    public Reservation(User user, Vendor vendor, List<Sky> skyReserved, List<Snowboard> snowboardReserved, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.vendor = vendor;
        this.skyReserved = skyReserved;
        this.snowboardReserved = snowboardReserved;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
