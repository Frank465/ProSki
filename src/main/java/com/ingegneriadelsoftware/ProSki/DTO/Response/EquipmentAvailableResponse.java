package com.ingegneriadelsoftware.ProSki.DTO.Response;

import com.ingegneriadelsoftware.ProSki.DTO.Utils.SkiDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.SnowboardDTO;
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
    String vendorEmail;
    List<SkiDTO> skisList;
    List<SnowboardDTO> snowboardsList;
    String message;
}
