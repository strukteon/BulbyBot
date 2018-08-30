package me.strukteon.bettercommand.syntax;
/*
    Created by nils on 02.04.2018 at 17:46.
    
    (c) nils 2018
*/

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyntaxBuilder {

    private List<Element> elements = new ArrayList<>();
    private SyntaxHandler syntaxHandler;

    private List<SyntaxBuilder> alternateBuilders = null;

    private int minArgs = 0;

    public SyntaxBuilder(){ }

    public SyntaxBuilder(SyntaxBuilder... syntaxBuilders){
        alternateBuilders = Arrays.asList(syntaxBuilders);
    }

    public SyntaxBuilder setErrorHandler(SyntaxHandler syntaxHandler) {
        this.syntaxHandler = syntaxHandler;
        return this;
    }

    public boolean build(String command, String[] args, MessageReceivedEvent event){
        if (syntaxHandler == null)
            syntaxHandler = new SyntaxHandler() {
                @Override
                public boolean onException(SyntaxValidateException e) {
                    return false;
                }

                @Override
                public void onFinish(Syntax syntax) {
                    // Nothing to do here
                }
            };
        if (alternateBuilders == null){
            return _build(command, args, event, 0);
        } else {
            return _buildAlternate(command, args, event);
        }
    }

    private boolean _build(String command, String[] args, MessageReceivedEvent event, int curBuilderNum) {
        if (minArgs > args.length) {
            syntaxHandler.onException(new SyntaxValidateException(SyntaxValidateException.Cause.TOO_FEW_ARGS));
            return false;
        }
        SyntaxValidator validator = new SyntaxValidator();
        Syntax syntax = new Syntax();
        syntax.setHelp(getAsHelp(command));
        int curElem = 0;
        int elemNum = elements.size();
        List<Object> parsedList = null;
        if (elements.size() > 0)
            for (int i = 0; i < args.length; i++){
            if (curElem >= elements.size())
                break;
                Element e = elements.get(curElem);
                String s = args[i];
                if (parsedList == null && e.multiple)
                    parsedList = new ArrayList<>();
                try {
                    Object parsed = validator.validate(s, e.element, event);
                    if (e.multiple) {
                        parsedList.add(parsed);
                        if (i == args.length - 1 || args.length - i == elemNum - curElem) {
                            syntax.put(e.element.getName(), parsedList);
                            curElem++;
                        }
                    } else {
                        syntax.put(e.element.getName(), parsed);
                        curElem++;
                    }
                } catch (SyntaxValidateException exception){
                    if (e.isOptional){

                        syntax.put(e.element.getName(), null);
                        curElem++;
                        i--;
                    } else
                        if (!syntaxHandler.onException(new SyntaxValidateException(exception.getErrorCause(), e.element))){
                            return false;
                        }

                }
            }
        syntax.setExecutedBuilder(curBuilderNum);
        syntaxHandler.onFinish(syntax);
        return true;
    }

    private boolean _buildAlternate(String command, String[] args, MessageReceivedEvent event){

        List<SyntaxValidateException> exceptions = new ArrayList<>();

        for (int i = 0; i < alternateBuilders.size(); i++){
            SyntaxBuilder b = alternateBuilders.get(i);
            b.setErrorHandler(new SyntaxHandler() {
                @Override
                public boolean onException(SyntaxValidateException e) {
                    exceptions.add(e);
                    return false;
                }

                @Override
                public void onFinish(Syntax syntax) {
                    syntaxHandler.onFinish(syntax);
                }
            });
            if (b._build(command, args, event, i))
                return true;
        }
        syntaxHandler.onException(new SyntaxValidateException(exceptions));
        return false;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Element e : elements){
            if (b.length() != 0)
                b.append(" ");
            if (e.element.getType().equals(SyntaxElementType.STRING_OF_LIST)){
                List<String> possibilities = ((SyntaxElement.SubCommand)e.element).getPossibilities();
                if (possibilities.size() == 1)
                    b.append(possibilities.get(0));
                else {
                    StringBuilder inner = new StringBuilder();
                    for (String s : possibilities) {
                        if (inner.length() != 0)
                            inner.append("|");
                        inner.append(s);
                    }
                    b       .append(e.isOptional ? "[" : "")
                            .append(inner)
                            .append(e.isOptional ? "]" : "");
                }
            } else {
                b.append(e.isOptional ? "*[" : "<");
                b.append(e.element.getName());
                if (e.multiple)
                    b.append("...");
                b.append(e.isOptional ? "]*" : ">");
            }
        }
        return b.toString();
    }

    public String getAsHelp(String command){
        StringBuilder syntax_ = new StringBuilder();
        for (SyntaxBuilder b : getAlternateBuilders()) {
            if (syntax_.length() != 0)
                syntax_.append(" \n ");
            syntax_.append(command).append(" ").append(b);
        }
        return syntax_.toString();
    }

    public SyntaxBuilder addElement(String name, SyntaxElementType type){
        return addElement(new SyntaxElement(name, type), false);
    }

    public SyntaxBuilder addElement(String name, SyntaxElementType type, boolean multiple){
        return addElement(new SyntaxElement(name, type), multiple);
    }

    public SyntaxBuilder addOptionalElement(String name, SyntaxElementType type){
        return addOptionalElement(new SyntaxElement(name, type), false);
    }

    public SyntaxBuilder addOptionalElement(String name, SyntaxElementType type, boolean multiple){
        return addOptionalElement(new SyntaxElement(name, type), multiple);
    }


    public SyntaxBuilder addElement(SyntaxElement element){
        return addElement(element, false);
    }

    public SyntaxBuilder addElement(SyntaxElement element, boolean multiple){
        elements.add(new Element(element, multiple, false));
        minArgs++;
        return this;
    }

    public SyntaxBuilder addOptionalElement(SyntaxElement element){
        return addOptionalElement(element, false);
    }

    public SyntaxBuilder addOptionalElement(SyntaxElement element, boolean multiple){
        elements.add(new Element(element, multiple, true));
        return this;
    }


    public SyntaxBuilder addSubcommand(String name, String... possibilities){
        addElement(new SyntaxElement.SubCommand(name, possibilities), false);
        return this;
    }

    public SyntaxBuilder addOptionalSubcommand(String name, String... possibilities){
        addOptionalElement(new SyntaxElement.SubCommand(name, possibilities), false);
        return this;
    }

    public List<SyntaxBuilder> getAlternateBuilders() {
        if (alternateBuilders == null) {
            List<SyntaxBuilder> sb = new ArrayList<>();
            sb.add(this);
            return sb;
        }
        return alternateBuilders;
    }

    private class Element {

        private SyntaxElement element;
        boolean multiple;
        boolean isOptional;
        public Element(SyntaxElement element, boolean multiple, boolean isOptional){
            this.element = element;
            this.multiple = multiple;
            this.isOptional = isOptional;
        }
    }

}
