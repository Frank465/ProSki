package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.Request.CommentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LocationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.CardSkipassRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Service.LocationService;
import com.ingegneriadelsoftware.ProSki.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final UserService userService;

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createLocation(@Valid @RequestBody LocationRequest request) {
        try{
            return ResponseEntity.ok(locationService.createLocation(request));
        }catch(IllegalStateException | DateTimeException enf){
            return new ResponseEntity<>(enf.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @PostMapping("/create/skipass")
    public ResponseEntity<String> createSkipass(@Valid @RequestBody CardSkipassRequest request) {
        try {
            return ResponseEntity.ok(locationService.createSkipass(request));
        }catch (IllegalStateException | EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("create/message")
    public ResponseEntity<String> createMessageToLocation(@Valid @RequestBody MessageRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(locationService.createMessage(request, httpRequest));
        } catch(EntityNotFoundException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create/comment")
    public ResponseEntity<?> createCommentToVendorMessage(@Valid @RequestBody CommentRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(locationService.createCommentToMessage(request, httpRequest));
        } catch(EntityNotFoundException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/all/message/{id_location}")
    public ResponseEntity<MessageResponse> gelAllMessageByLocation(@PathVariable("id_location") Integer idLocation) {
        try{
            return ResponseEntity.ok(locationService.getAllMessage(idLocation));
        } catch(IllegalStateException e) {
            return new ResponseEntity<>(MessageResponse.builder().error(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }

    }


}
