package com.readinglength.researcherws.lib;

public class BookNotFoundException extends Exception{

    private String message;

    public BookNotFoundException(String title, String service) {
        this.message = String.format("'%s' not found on %s.", title, service);
    }

    public String getMessage() {
        return message;
    }
}
