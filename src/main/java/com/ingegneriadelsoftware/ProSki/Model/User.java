package com.ingegneriadelsoftware.ProSki.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Integer userId;

    @Column
    private String name;

    @Column
    private String surname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String token;

    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;

    @Column
    private LocalDate dateBirth;

    private boolean locked = false;
    private boolean enable = false;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    private Plan plan;

    @OneToMany(mappedBy = "user")
    private List<BuySkipass> buySkipasses;

    @ManyToMany
    @JoinTable(name = "users_lessons", joinColumns =
    @JoinColumn(name = "user"), inverseJoinColumns =
    @JoinColumn(name = "lesson"))
    private List<Lesson> usersLessons;

    @OneToMany(mappedBy = "user")
    List<VendorMessage> vendorMessages;

    @OneToMany(mappedBy = "user")
    List<LocationMessage> locationMessages;

    @OneToMany(mappedBy = "user")
    List<InstructorMessage> instructorMessages;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String email){
        this.email = email;
    }

    public User(String name, String surname, String password, Gender gender, LocalDate dateBirth, String email, Role role) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.dateBirth = dateBirth;
        this.role = role;
    }
    public User(String password, String email, Role role, boolean enable) {
        this.password = password;
        this.email = email;
        this.role = role;
        this.enable = enable;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
