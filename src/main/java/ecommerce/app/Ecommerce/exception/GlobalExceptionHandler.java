package ecommerce.app.Ecommerce.exception;

import ecommerce.app.Ecommerce.dtos.ExceptionDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(ResourceNotFoundException.class)
 public ResponseEntity<ExceptionDto>  handlerResourceNotFound(ResourceNotFoundException ex , HttpServletRequest req){
     ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now() , HttpStatus.NOT_FOUND.value() , HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage() , req.getRequestURI());
     return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
 }
 @ExceptionHandler(ResourceAlreadyExist.class)
 public ResponseEntity<ExceptionDto>  handlerResourceNotFound(ResourceAlreadyExist ex , HttpServletRequest req){
     ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now() , HttpStatus.CONFLICT.value() , HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage() , req.getRequestURI());
     return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
 }

 @ExceptionHandler(BadRequestException.class)
 public ResponseEntity<ExceptionDto> handleBadRequest(BadRequestException ex , HttpServletRequest req) {
     ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now() , HttpStatus.BAD_REQUEST.value() , HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage() , req.getRequestURI());
     return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
 }

}
