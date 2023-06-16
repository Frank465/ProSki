package com.ingegneriadelsoftware.ProSki.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La Entity è per i commenti di un utente su un messaggio relativo ad una località
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LocationComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_locationMessage")
    private LocationMessage locationMessage;

    @Column
    private String comment;

    public LocationComment(User user, LocationMessage locationMessage, String comment) {
        this.user = user;
        this.locationMessage = locationMessage;
        this.comment = comment;
    }
}
