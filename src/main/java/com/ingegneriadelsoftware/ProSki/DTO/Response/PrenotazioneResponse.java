package com.ingegneriadelsoftware.ProSki.DTO.Response;


import com.ingegneriadelsoftware.ProSki.Model.Sci;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrenotazioneResponse {
    private String nomeUtente;
    private String nomeRifornitore;
    private String dataPrenotazione;
    private String dataDeposito;
    private List<Sci> listaSci;
    private List<Snowboard> listaSnowboards;
}
