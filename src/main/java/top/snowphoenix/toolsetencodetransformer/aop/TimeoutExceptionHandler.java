package top.snowphoenix.toolsetencodetransformer.aop;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.snowphoenix.toolsetencodetransformer.exception.TimeoutException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class TimeoutExceptionHandler {

    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(TimeoutException e, HttpServletResponse response) {
        response.setHeader("SP-TIME-OUT", "");
    }
}
