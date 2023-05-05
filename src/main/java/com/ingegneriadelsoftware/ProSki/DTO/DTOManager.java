package com.ingegneriadelsoftware.ProSki.DTO;

import com.ingegneriadelsoftware.ProSki.DTO.Request.LocalitaRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MaestroRequest;
import com.ingegneriadelsoftware.ProSki.Model.Localita;
import com.ingegneriadelsoftware.ProSki.Model.Maestro;

public class DTOManager {

    public static Localita getLocalitaByLocalitaRequest(LocalitaRequest request) {
        Localita localita = new Localita();
        localita.setNome(request.getNome());
        localita.setPrezzoAbbonamento(request.getPrezzoAbbonamento());
        return localita;
    }

    public static Maestro getMaestroByMaestroRequest(MaestroRequest request) {
        Maestro maestro = new Maestro();
        maestro.setNome(request.getNome());
        maestro.setCognome(request.getCognome());
        maestro.setEmail(request.getEmail());
        maestro.setSpecialita(request.getSpecialita());
        return maestro;
    }
}
