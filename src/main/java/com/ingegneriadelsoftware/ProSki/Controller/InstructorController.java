package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.Request.InstructorRequest;
import com.ingegneriadelsoftware.ProSki.Service.InstructorService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/instructor")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @PostMapping("create")
    public ResponseEntity<String> createInstructor(@Valid @RequestBody InstructorRequest request) {
        try {
            return ResponseEntity.ok(instructorService.insertInstructor(request));
        }catch (IllegalStateException | EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
