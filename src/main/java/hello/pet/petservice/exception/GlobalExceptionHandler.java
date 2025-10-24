package hello.pet.petservice.exception;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> forbiddenExceptionHandler(ForbiddenException e) {
        return generateExceptionResponse(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> entityNotFoundExceptionHandler(EntityNotFoundException e) {
        return generateExceptionResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyAnnouncedException.class)
    public ResponseEntity<ExceptionResponse> alreadyAnnouncedExceptionHandler(AlreadyAnnouncedException e) {
        return generateExceptionResponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> responseExceptionHandler(Exception e) {
        return generateExceptionResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionResponse> generateExceptionResponse(Exception e, HttpStatus status) {
        ExceptionResponse response = ExceptionResponse.of(e, status.value(), status.name());
        return ResponseEntity.status(status).body(response);
    }
}
