package exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiRequestException extends RuntimeException{
    public HttpStatus status;

    public ApiRequestException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }
    public ApiRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}