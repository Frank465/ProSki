package com.ingegneriadelsoftware.ProSki.DTO.Response;

import com.ingegneriadelsoftware.ProSki.Model.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Integer userId;
    private String name;
    private String surname;
    private String email;
    private Integer age;
    private Gender gender;
    private String plan;
}
