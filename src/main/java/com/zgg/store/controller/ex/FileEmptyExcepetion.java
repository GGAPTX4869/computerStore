package com.zgg.store.controller.ex;


public class FileEmptyExcepetion extends FileUploadException{
    public FileEmptyExcepetion() {
    }

    public FileEmptyExcepetion(String message) {
        super(message);
    }

    public FileEmptyExcepetion(String message, Throwable cause) {
        super(message, cause);
    }

    public FileEmptyExcepetion(Throwable cause) {
        super(cause);
    }

    public FileEmptyExcepetion(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
