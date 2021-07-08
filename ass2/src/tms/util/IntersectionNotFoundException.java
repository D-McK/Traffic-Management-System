package tms.util;

/**
 * Exception thrown when a request is made for a route that does not exist.
 */

public class IntersectionNotFoundException extends Exception {

    /**
     Constructs a normal IntersectionNotFoundException with no error message or
     cause.
     */

    public IntersectionNotFoundException() { super(); }
}
