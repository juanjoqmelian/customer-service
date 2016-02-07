package com.dragosolutions.microservices.customer.exception;


import javax.ws.rs.WebApplicationException;

public class CustomerNotFoundException extends WebApplicationException {

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
