package me.strukteon.bettercommand.syntax;
/*
    Created by nils on 02.04.2018 at 17:50.
    
    (c) nils 2018
*/

public enum SyntaxElementType {

    INT,
    STRING,
    LONG,
    USER,
    MEMBER,
    ID,
    GUILD,
    TEXTCHANNEL,
    VOICECHANNEL,
    ROLE,
    STRING_OF_LIST;


    public String getRegexMatcher(){
        switch (this){
            case INT:
                return "[0-9]{1,9}";
            case STRING:
                return ".+";
            case LONG:
                return "[0-9]{1,18}";
            default:
                return ".+";
        }
    }

}
