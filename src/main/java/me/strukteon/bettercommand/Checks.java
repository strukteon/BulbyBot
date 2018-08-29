package me.strukteon.bettercommand;
/*
    Created by nils on 25.08.2018 at 00:40.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.syntax.SyntaxValidateException;

import java.util.Arrays;

public abstract class Checks {

    public static Object syntaxNotNull(Returner<Object> returner, Object... checked) throws SyntaxValidateException {
        if (Arrays.asList(checked).contains(null))
            return null;
        try {
            return returner.execute();
        } catch (Exception e) {
            throw (SyntaxValidateException)e;
        }
    }

    public interface Returner<T> {
        T execute() throws Exception;
    }

}
