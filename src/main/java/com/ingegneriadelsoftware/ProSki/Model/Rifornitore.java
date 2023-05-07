package com.ingegneriadelsoftware.ProSki.Model;

import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rifornitore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rifornitoreId;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_localita", nullable = false)
    private Localita localita;

    @OneToMany(mappedBy = "rifornitore")
    private List<Prenotazione> prenotazioni;

    @OneToMany(mappedBy = "rifornitore")
    private List<Sci> sciDisponibili;

    @OneToMany(mappedBy = "rifornitore")
    private List<Snowboard> snowboardDisponibili;

}
