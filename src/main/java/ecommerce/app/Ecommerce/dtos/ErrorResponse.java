package ecommerce.app.Ecommerce.dtos;

public class ErrorResponse {
    private String code;       // INVALID_REQUEST, CONFLICT, INVALID_SIGNATURE, SERVER_ERROR ...
    private String message;







    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
