package kimSeulHa.stockDividend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException e){
        //내가 정의한 에러가 일어났을 경우
        ErrorResponse er = ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(er, HttpStatus.resolve(e.getStatusCode()));
    }

}
