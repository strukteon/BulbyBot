package me.strukteon.bulbybot;/*
    Created by nils on 22.08.2018 at 20:00.
    
    (c) nils 2018
*/

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Random;

public class Test {

    public String s = "LOLOLOL";
    public static void main(String[] args) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        engine.put("i", 12);
        try { //getAsSimpleClass(Test.class, engine)
            //engine.put("StringBuilder", getAsSimpleClass(StringBuilder.class, engine));
            engine.put("Test", engine.eval("Packages." + Test.class.getName()));
            engine.put("parent", new Test());
            System.out.println(engine.eval("new java.lang.StringBuilder().append(1).toString();"));
            System.out.println(engine.eval("Test.retard('ich kann java')"));
            System.out.println(engine.eval("parent.s"));

            //System.out.println(engine.eval("while (true) print(i);"));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public static String retard(String s){
        StringBuilder sb = new StringBuilder();
        char[] chars = new char[s.length()];
        s.getChars(0, s.length(), chars, 0);
        Random r = new Random();
        for (char c : chars)
            if (r.nextBoolean())
                sb.append(Character.toUpperCase(c));
            else
                sb.append(Character.toLowerCase(c));
        return sb.toString();
    }

    public static Object getAsSimpleClass(Class class_, ScriptEngine engine){
        try {
            return engine.eval(class_.getName());
        } catch (ScriptException e) {
            System.out.println(e);
            return null;
        }
    }
}
