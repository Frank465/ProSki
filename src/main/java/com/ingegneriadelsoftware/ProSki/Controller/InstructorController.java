package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.CommentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.InstructorRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.Service.InstructorService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/v1/instructor")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("create")
    public ResponseEntity<String> createInstructor(@Valid @RequestBody InstructorRequest request) {
        try {
            return ResponseEntity.ok(instructorService.insertInstructor(request));
        }catch (IllegalStateException | EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("create/message")
    public ResponseEntity<?> createMessageToInstructor(@Valid @RequestBody MessageRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(instructorService.createMessage(request, httpRequest));
        } catch(EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create/comment")
    public ResponseEntity<?> createCommentToInstructorMessage(@Valid @RequestBody CommentRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(instructorService.createCommentToMessage(request, httpRequest));
        } catch(EntityNotFoundException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/get/all/message/{id_instructor}")
    public ResponseEntity<MessageResponse> getAllMessageByInstructor(@PathVariable("id_instructor") Integer idLocation) {
        try{
            return ResponseEntity.ok(instructorService.getAllMessage(idLocation));
        } catch(IllegalStateException e) {
            return new ResponseEntity<>(MessageResponse.builder().error(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/message")
    public ResponseEntity<?> deleteMessageByInstructor(@RequestBody MessageDTO request) {
        try {
            return ResponseEntity.ok(instructorService.deleteMessage(request));
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("delete/comments")
    public ResponseEntity<?> deleteCommentFromMessageByInstructor(@RequestBody MessageDTO request) {
        try {
            return ResponseEntity.ok(instructorService.deleteComments(request));
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
