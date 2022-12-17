package ru.nsu.fit.tsibin.exceptions;

import java.io.IOException;

public class IncorrectResponseException extends IOException {
    public IncorrectResponseException(){
    }

    public IncorrectResponseException(int errorCode){
        super("incorrect response, error code: " + errorCode);
    }
}
