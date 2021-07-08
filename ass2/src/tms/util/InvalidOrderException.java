package tms.util;

/**
 * Exception thrown when traffic lights are created with an invalid ordering of
 * incoming routes, ie. the order is not a permutation of the intersection's
 * list of incoming routes.
 */
public class InvalidOrderException extends Exception {

    /**
     * Constructs a normal InvalidOrderException with no error message or cause.
     */
    public InvalidOrderException() { super(); }
}