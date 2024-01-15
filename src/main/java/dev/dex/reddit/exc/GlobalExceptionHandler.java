package dev.dex.reddit.exc;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOG.info("Resolved " + ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> handleException(DuplicateKeyException ex, HttpServletRequest request) {
        LOG.info("DuplicateKeyException Resolved");
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                status.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(apiError);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleException(Exception ex, HttpServletRequest request) {
//        HttpStatus status = HttpStatus.BAD_REQUEST;
//        ApiError apiError = new ApiError(
//                request.getRequestURI(),
//                ex.getMessage(),
//                status.value(),
//                LocalDateTime.now()
//        );
//        return ResponseEntity.status(status).body(apiError);
//    }
}
