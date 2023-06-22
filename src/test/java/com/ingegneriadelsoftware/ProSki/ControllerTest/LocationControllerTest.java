/*
package com.ingegneriadelsoftware.ProSki.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LocationRequest;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Service.LocationService;
import com.ingegneriadelsoftware.ProSki.Stub;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.ArrayList;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is


@WebMvcTest(controllers = LocationController.class)
@ExtendWith(MockitoExtension.class)
public class LocationControllerTest {

    private static final String END_POINT_PATH = "/api/v1/location";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void LocationController_CreateLocation_ReturnOk() throws Exception {
        LocationRequest request = Stub.locationDTOStub();

        Location location = new Location();
        location.setName(request.getName());

        given(locationService.createLocation(any())).willReturn(location);
        ResultActions response = mockMvc.perform(post(END_POINT_PATH+"/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Stub.locationDTOStub())));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name", is("Marileva")))
                .andExpect(jsonPath("$.priceSubscription", is(35.50)))
                .andExpect(jsonPath("$.startOfSeason", is("12/12/2023")))
                .andExpect(jsonPath("$.endOfSeason", is("12/03/2024")))
                .andExpect(jsonPath("$.openingSkiLift", is("09:00")))
                .andExpect(jsonPath("$.closingSkiLift", is("16:00")));
    }

    @Test
    public void LocationControlle_CreateLocation_ReturnBadRequest() throws Exception {
        LocationRequest request = new LocationRequest();
        request.setName("");
        request.setEndOfSeason("null");
        request.setPriceSubscription(0.0);
        String requestBody = objectMapper.writeValueAsString(request);

        given(locationService.createLocation(any())).willAnswer((invocation -> invocation.getArgument(0)));

        mockMvc.perform(post(END_POINT_PATH+"/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
*/
