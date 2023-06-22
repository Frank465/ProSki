package com.ingegneriadelsoftware.ProSki.Mapping;

import com.ingegneriadelsoftware.ProSki.DTO.Request.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.RegisterRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.UserPlanRequest;
import com.ingegneriadelsoftware.ProSki.Model.Plan;
import com.ingegneriadelsoftware.ProSki.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Request {

    public static RegisterRequest registerRequestMapper(User user) {
        RegisterRequest request = new RegisterRequest();
        request.setName(user.getName());
        request.setSurname(user.getSurname());
        request.setEmail(user.getEmail());
        request.setGender(user.getGender().toString());
        request.setPassword(user.getPassword());
        request.setDateBirth(user.getDateBirth().toString());
        return request;
    }

    public static AuthenticationRequest authenticationRequestMapper(User user) {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail(user.getEmail());
        request.setPassword(user.getPassword());
        return request;
    }
}
