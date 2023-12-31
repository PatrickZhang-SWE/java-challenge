package jp.co.axa.apidemo.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * for exceptions handling
 */
@ControllerAdvice
@Slf4j
public class EmployeeExceptionHandler {

    @ExceptionHandler(ResourcesNotFoundException.class)
    public ResponseEntity<Object> catchNotFoundException(ResourcesNotFoundException notFoundException){
        log.error("Resource is not found.", notFoundException);
        return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    //exception for controller.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object>  catchValidationException(MethodArgumentNotValidException argumentNotValidException){
        log.error("Validation exception happens:",argumentNotValidException);
        Map<String,String> errorMessageMap = new HashMap<>();
        argumentNotValidException.getBindingResult().getAllErrors().forEach(error->{
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errorMessageMap.put(fieldName,errorMessage);
        });
        return new ResponseEntity<>(errorMessageMap.toString(),HttpStatus.BAD_REQUEST);
    }

    //exception for persist
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object>  catchValidationException(ConstraintViolationException constraintViolationException){
        log.error("Validation exception happens:",constraintViolationException);
        return new ResponseEntity<>(constraintViolationException.getMessage(),HttpStatus.BAD_REQUEST);
    }
}
