package com.uca;

/**
 * Classe de l'exception levée
 * dans le cas où un paramètre ou une entrée fournie par le client est invalide
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
