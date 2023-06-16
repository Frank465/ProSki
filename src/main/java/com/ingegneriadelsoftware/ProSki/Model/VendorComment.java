package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La Entity è per i commenti di un utente su un messaggio relativo ad un rifornitore
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VendorComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_vendorMessage")
    private VendorMessage vendorMessage;

    @Column
    private String comment;

    public VendorComment(User user, VendorMessage vendorMessage, String comment) {
        this.user = user;
        this.vendorMessage = vendorMessage;
        this.comment = comment;
    }
}
