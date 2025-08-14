package io.github.joachimdi.exceptions;

public class PortosyncApiClientException extends RuntimeException {

    public PortosyncApiClientException(String message) {
        super(message);
    }

    public PortosyncApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
