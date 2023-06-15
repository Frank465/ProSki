package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VendorMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "id_vendor")
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column
    private String message;

    public VendorMessage(Vendor vendor, User user, String message) {
        this.vendor = vendor;
        this.user = user;
        this.message = message;
    }
}
