package me.strukteon.bettercommand.syntax;
/*
    Created by nils on 02.04.2018 at 22:11.
    
    (c) nils 2018
*/

public interface SyntaxHandler {

    public boolean onException(SyntaxValidateException e); // false = stop parsing; true = keep running

    public void onFinish(Syntax syntax);

}
