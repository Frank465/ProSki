package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InstructorMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "id_instructor")
    private Instructor instructor;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column
    private String message;

    public InstructorMessage(Instructor instructor, User user, String message) {
        this.instructor = instructor;
        this.user = user;
        this.message = message;
    }
}
