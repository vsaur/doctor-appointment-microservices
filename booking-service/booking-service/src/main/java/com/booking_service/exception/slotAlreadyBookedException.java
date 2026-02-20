package com.booking_service.exception;

public class slotAlreadyBookedException extends RuntimeException{
    public slotAlreadyBookedException(String message){
        super(message);
    }
}
