package me.strukteon.bettercommand.syntax;
/*
    Created by nils on 02.04.2018 at 17:46.
    
    (c) nils 2018
*/


import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SyntaxElement {

    private SyntaxElementType type;

    private String name;

    private String regexMatcher;


    public SyntaxElement(String name, SyntaxElementType type){
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public SyntaxElementType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getName() + " (" + getType() + ")";
    }

    public static class SubCommand extends SyntaxElement {
        private List<String> possibilities;
        private String regexMatcher;

        public SubCommand(String name, String... possibilities){
            super(name, SyntaxElementType.STRING_OF_LIST);
            this.possibilities = Arrays.asList(possibilities);
            StringBuilder b = new StringBuilder();
            b.append("(");
            for (String s : possibilities)
                b.append(Pattern.quote(s))
                .append("|");
            b.deleteCharAt(b.length()-1)
                    .append(")");
            this.regexMatcher = b.toString();
        }

        public String getRegexMatcher() {
            return regexMatcher;
        }

        public List<String> getPossibilities() {
            return possibilities;
        }
    }

}
