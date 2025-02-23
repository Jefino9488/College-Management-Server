package com.CollegeManager.CollegeManagerServer.exceptionHandler;

import com.CollegeManager.CollegeManagerServer.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ExceptionDTO> HandleExceptionResolver(Exception ex){
        var exceptiondto = ExceptionDTO.builder()
                .errorHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptiondto);
    }

}