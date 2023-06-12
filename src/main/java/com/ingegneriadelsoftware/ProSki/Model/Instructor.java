package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String speciality;

    @ManyToOne
    @JoinColumn(name = "id_location")
    private Location location;

    @OneToMany(mappedBy = "instructor")
    private List<Lesson> lessonList;

    public Instructor(String name, String surname, String email, String speciality, Location location) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.speciality = speciality;
        this.location = location;
    }
}
