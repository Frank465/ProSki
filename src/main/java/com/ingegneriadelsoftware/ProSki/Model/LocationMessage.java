package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LocationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "id_location")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column
    private String message;

    public LocationMessage(Location location, User user, String message) {
        this.location = location;
        this.user = user;
        this.message = message;
    }
}
