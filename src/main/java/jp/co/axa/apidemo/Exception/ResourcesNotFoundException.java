package jp.co.axa.apidemo.Exception;

public class ResourcesNotFoundException extends RuntimeException {
    public ResourcesNotFoundException(String message){
        super(message);
    }
}
