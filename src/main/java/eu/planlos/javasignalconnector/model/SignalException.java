package eu.planlos.javasignalconnector.model;

public class SignalException extends RuntimeException {

    public static final String IS_NULL = "ApiResponse object is NULL";

    public SignalException(String message) {
        super(message);
    }
}
