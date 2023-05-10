package com.ingegneriadelsoftware.ProSki.DTO.Request;

import com.ingegneriadelsoftware.ProSki.Model.Sci;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 *La classe di utilità viene utilizzata per la request poi nel DTOManager verrà mappata come una vera Prenotazione
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class PrenotazioneRequest {

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String emailRifornitore;

    @NotNull
    private List<Sci> sciList;

    @NotNull
    private List<Snowboard> snowboardsList;

    @NotNull
    @NotEmpty
    @DateTimeFormat(pattern="dd/MM/yyyy")
    private String dataInizio;

    @NotNull
    @NotEmpty
    @DateTimeFormat(pattern="dd/MM/yyyy")
    private String dataFine;
}
