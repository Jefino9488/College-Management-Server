package com.CollegeManager.CollegeManagerServer.exceptionHandler;

import com.CollegeManager.CollegeManagerServer.dto.ExceptionDTO;
import jakarta.persistence.EntityNotFoundException; // Import
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // Import for validation
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionController {

    // Handler for validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        var exceptionDto = ExceptionDTO.builder()
                .errorHttpStatus(HttpStatus.BAD_REQUEST.value())
                .errorMessage(errorMessage)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }

    // Handler for "Not Found" errors
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleEntityNotFoundException(EntityNotFoundException ex) {
        var exceptionDto = ExceptionDTO.builder()
                .errorHttpStatus(HttpStatus.NOT_FOUND.value())
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
    }

    // Generic handler for other exceptions
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ExceptionDTO> handleGenericException(Exception ex){
        var exceptionDto = ExceptionDTO.builder()
                .errorHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("An unexpected error occurred: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDto);
    }
}