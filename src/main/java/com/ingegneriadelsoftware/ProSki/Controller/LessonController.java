package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LessonRequest;
import com.ingegneriadelsoftware.ProSki.Service.LessonService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;

@RestController
@RequestMapping("/api/v1/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createLesson(@Valid @RequestBody LessonRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toLessonResponseByLesson(lessonService.createLesson(request)));
        } catch (IllegalStateException | EntityNotFoundException | DateTimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getLessons() {
        try{
            return ResponseEntity.ok(lessonService.getListLessons());
        }catch(EntityNotFoundException ex) {
            return new ResponseEntity<>("Non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}