package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * La Entity è per i commenti di un utente su un messaggio di un fornitore
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InstructorComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "instructorMessage")
    private InstructorMessage instructorMessage;

    @Column
    private String comment;

    public InstructorComment(User user, InstructorMessage instructorMessage, String comment) {
        this.user = user;
        this.instructorMessage = instructorMessage;
        this.comment = comment;
    }
}
