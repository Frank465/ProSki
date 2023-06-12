package com.ingegneriadelsoftware.ProSki.DTO.Request;

import com.ingegneriadelsoftware.ProSki.Model.Sky;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class VendorEquipmentRequest {

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String vendorEmail;

    @NotNull
    private List<Sky> sky;

    @NotNull
    private List<Snowboard> snowboards;

}
