package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.BuySkipassRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.UserPlanRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.BuySkipassResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LessonResponse;
import com.ingegneriadelsoftware.ProSki.Model.Lesson;
import com.ingegneriadelsoftware.ProSki.Service.LessonService;
import com.ingegneriadelsoftware.ProSki.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LessonService lessonService;

    @GetMapping("/lezioni")
    public ResponseEntity<?> getAllLessonsByUtente(HttpServletRequest servletRequest) {
        List<LessonResponse> lessonsResponse = new ArrayList<>();
        try{
            List<Lesson> lessons = userService.getLezioniByUtente(servletRequest);
            lessons.forEach(cur-> {
                lessonsResponse.add(DTOManager.toLessonResponseByLesson(cur));
            });
            return ResponseEntity.ok(lessonsResponse);
        }catch(EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/iscrizione/lezione")
    public ResponseEntity<String> registrationUserLesson(@RequestParam("id") Integer lessonId, HttpServletRequest servletRequest) {
        try{
            return ResponseEntity.ok(userService.registrationLesson(lessonId, servletRequest));
        }catch(EntityNotFoundException | IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/tutte/lezioni")
    public ResponseEntity<?> getLessons() {
        try{
            return ResponseEntity.ok(lessonService.getListLessons());
        }catch(EntityNotFoundException ex) {
            return new ResponseEntity<>("Non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @PostMapping("/insert/user/plan")
    public ResponseEntity<String> enterUserPlan(@Valid @RequestBody UserPlanRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(userService.insertUserPlan(request));
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/buy/skipass")
    public ResponseEntity<BuySkipassResponse> buySkipass(@Valid @RequestBody BuySkipassRequest request, HttpServletRequest httpRequest) {
        try{
            BuySkipassResponse buySkipassResponse = DTOManager.toBuySkipassResponseByBuySkipass(userService.buySkipassUser(request, httpRequest));
            return ResponseEntity.ok(buySkipassResponse);
        }catch(IllegalStateException e) {
            return new ResponseEntity<>(BuySkipassResponse.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @GetMapping("/getAllUsers/byGender/{gender}")
    public ResponseEntity<?> getUsersByGender(@PathVariable String gender) {
        try {
            return ResponseEntity.ok(DTOManager.toUsersResponseByUsers(userService.getAllUsersByGender(gender)));
        }catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @GetMapping("/getAllUsers/byAge")
    public ResponseEntity<?> getUsersByAge(@PathParam("startAge") Integer startAge, @PathParam("endAge") Integer endAge) {
        try{
            return ResponseEntity.ok(DTOManager.toUsersResponseByUsers(userService.getUsersByAgeBetween(startAge, endAge)));
        }catch(IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @DeleteMapping("/delete/user/{email}")
    public ResponseEntity<String> deleteUtente(@PathVariable("email") String email) {
        try{
            return ResponseEntity.ok(userService.deleteUserByEmail(email));
        }catch(IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
