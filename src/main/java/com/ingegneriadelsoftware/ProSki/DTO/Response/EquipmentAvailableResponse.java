package com.ingegneriadelsoftware.ProSki.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipmentAvailableResponse {
    List<Integer> skyList;
    List<Integer> snowboardList;
}
