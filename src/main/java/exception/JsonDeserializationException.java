package exception;

public class JsonDeserializationException extends Exception{
    public JsonDeserializationException(String message) {
        super(message);
    }

    public JsonDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
