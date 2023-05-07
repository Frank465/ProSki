package com.ingegneriadelsoftware.ProSki.DTO.Request;

import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
import com.ingegneriadelsoftware.ProSki.Model.Sci;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class PrenotazioneRequest {

    @NotNull
    @NotEmpty
    private Utente utente;

    @NotNull
    @NotEmpty
    private Rifornitore rifornitore;

    @NotNull
    @NotNull
    private List<Sci> sci;

    @NotNull
    @NotNull
    private List<Snowboard> snowboards;

    @NotNull
    @NotEmpty
    @Past
    @Pattern(regexp = Utils.REGEX_DATA, message = Utils.ERROR_DATA)
    private String dataInizio;

    @NotNull
    @NotEmpty
    @Past
    @Pattern(regexp = Utils.REGEX_DATA, message = Utils.ERROR_DATA)
    private String dataFine;
}
