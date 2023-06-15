package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vendorId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_location")
    private Location location;

    @OneToMany(mappedBy = "vendor")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "vendor")
    private List<Ski> skiAvailable;

    @OneToMany(mappedBy = "vendor")
    private List<Snowboard> snowboardAvailable;

    @OneToMany(mappedBy = "vendor")
    private List<VendorMessage> vendorMessages;

    public Vendor(String name, String email, Location location) {
        this.name = name;
        this.email = email;
        this.location = location;
    }
}
