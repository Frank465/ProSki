package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LocalDateTime startLesson;

    @Column
    private LocalDateTime endLesson;

    @ManyToMany(mappedBy = "usersLessons")
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "id_instructor")
    private Instructor instructor;

    public Lesson(Instructor instructor, LocalDateTime startLesson, LocalDateTime endLesson) {
        this.startLesson = startLesson;
        this.endLesson = endLesson;
        this.instructor = instructor;
    }
}
