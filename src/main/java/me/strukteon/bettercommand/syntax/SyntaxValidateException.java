package me.strukteon.bettercommand.syntax;
/*
    Created by nils on 02.04.2018 at 18:05.
    
    (c) nils 2018
*/

import java.util.ArrayList;
import java.util.List;

public class SyntaxValidateException extends Exception {

    private SyntaxElement element;
    private Cause cause;

    private List<SyntaxValidateException> exceptions = new ArrayList<>();

    public SyntaxValidateException(String message){
        this(message, null);
    }

    public SyntaxValidateException(String message, SyntaxElement element){
        super(message);
        this.element = element;
    }

    public SyntaxValidateException(Cause cause){
        this(cause, null);
    }

    public SyntaxValidateException(Cause cause, SyntaxElement element){
        super("At: " + element + "; Cause: " + cause.name());
        this.cause = cause;
        this.element = element;
    }

    public SyntaxValidateException(List<SyntaxValidateException> exceptions){
        this.exceptions = exceptions;
    }

    public List<SyntaxValidateException> getExceptions() {
        if (exceptions.size() == 0)
            exceptions.add(this);
        return exceptions;
    }

    public SyntaxElement getElement() {
        return element;
    }

    public Cause getErrorCause() {
        return cause;
    }

    public enum Cause {

        NOT_FOUND,
        INVALID,
        EMPTY,
        TOO_FEW_ARGS,
        UNDEFINED,
        MISSING

    }
}
