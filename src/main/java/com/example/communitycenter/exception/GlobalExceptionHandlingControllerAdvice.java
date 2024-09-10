package com.example.communitycenter.exception;

import com.example.communitycenter.exception.utils.ConflictException;
import com.example.communitycenter.exception.utils.ErrorDTO;
import com.example.communitycenter.exception.utils.MessageService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandlingControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlingControllerAdvice.class);
    private final MessageService messageService;

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO handleConflictException(ConflictException ex) {
        logger.error("ConflictException - " + ex.toString());
        return ErrorDTO.builder()
                .code("409")
                .detail(ex.getMessage())
                .build();
    }


    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorDTO errorHandler(Exception ex) {
        logger.error("Exception - " + ex.toString());
        return ErrorDTO.builder()
                .code("500")
                .detail(ex.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO processValidationError(MethodArgumentNotValidException ex) {
        logger.error("MethodArgumentNotValidException - " + ex.toString());
        BindingResult result = ex.getBindingResult();
        List<String> errorMessages = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        return ErrorDTO.builder()
                .code("400")
                .detail(String.join(", ", errorMessages))
                .build();
    }
}