package com.ingegneriadelsoftware.ProSki.DTO.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class LocalitaRequest {

    @NotNull
    @NotEmpty
    private String nome;

    @NotNull
    @NotEmpty
    private Double prezzoAbbonamento;

}

