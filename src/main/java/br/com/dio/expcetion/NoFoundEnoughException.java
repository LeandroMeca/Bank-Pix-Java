package br.com.dio.expcetion;

public class NoFoundEnoughException extends RuntimeException {
    public NoFoundEnoughException(String message) {
        super(message);
    }
}
