package ec.edu.ups.icc.fundamentos01.core.exceptions.response;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String error;
    private String path;
    private Map<String, String> details;

    public ErrorResponse(HttpStatus status, String message, String path, Map<String, String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value(); // 404, 200, 500
        this.error = status.getReasonPhrase(); // NOT FOUND, OK, INTERNAL SERVER ERROR
        this.message = message;
        this.path = path;
        this.details = details;
    }
    
    public ErrorResponse(HttpStatus status, String message, String path) {
        this(status, message, path, null);
    }
    
    public ErrorResponse(HttpStatus status, String message) {
        this(status, message, null, null);
    }
    
    public ErrorResponse(HttpStatus status) {
        this(status, null, null, null);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getDetails() {
        return details;
    }
}
