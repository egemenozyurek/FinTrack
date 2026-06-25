package com.profileinsight.fintrack.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " cannot be found. ID: " + id);
    }
}