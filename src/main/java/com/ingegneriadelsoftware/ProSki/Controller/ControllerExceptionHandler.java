package com.ingegneriadelsoftware.ProSki.Controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Nella classe ci sono tutte le eccezioni che non vengono gestite nei controller
 * qui è possibile gestire tutte le eccezione in modo centralizzato.
 */
@RestControllerAdvice
@AllArgsConstructor
public class ControllerExceptionHandler {

    /**
     * Il metodo cattura tutte le eccezioni che riguardano le request dto, se i campi non rispettano javax.validator.constraints.
     * Gestisce se i campi sono vuoti o nulli e se le email non rispettano il giusto pattern.
     * @param ex
     * @return ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleExceptionHibernateValidator(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String errorMessage = fieldError.getDefaultMessage();
            errors.add(errorMessage);
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
