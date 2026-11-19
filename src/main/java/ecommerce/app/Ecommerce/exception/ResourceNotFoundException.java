package ecommerce.app.Ecommerce.exception;

public class ResourceNotFoundException extends  RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
