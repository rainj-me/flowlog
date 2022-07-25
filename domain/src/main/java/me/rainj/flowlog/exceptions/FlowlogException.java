package me.rainj.flowlog.exceptions;

/**
 * Flowlog exception, unchecked exception
 */
public class FlowlogException extends RuntimeException {

    /**
     * Constructor
     * @param message error message.
     */
    public FlowlogException(String message) {
        super(message);
    }
}
