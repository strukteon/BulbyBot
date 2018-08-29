package me.strukteon.bulbybot.core;
/*
    Created by nils on 28.04.2018 at 22:26.
    
    (c) nils 2018
*/


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CLI {

    private static File file = null;

    public static final String RESET = "\u001B[0m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BLACK_BACKGROUND = "\u001B[40m";
    public static final String RED_BACKGROUND = "\u001B[41m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String WHITE_BACKGROUND = "\u001B[47m";

    public static void setLogging(File file){
        CLI.file = file;
    }

    public static void error(Exception e){
        e.printStackTrace();
        /*error(e.getMessage());
        for (StackTraceElement el :e.getStackTrace())
            System.out.println(RED + "at " + el);*/
    }

    public static void error(String text){
        System.out.println(RED + log("[ERROR] " + text) + RESET);
    }

    public static void info(String text){
        System.out.println(BLUE + log("[INFO] " + text) + RESET);
    }

    public static void success(String text){
        System.out.println(GREEN + log("[SUCCESS] " + text) + RESET);
    }

    public static void debug(String text){
        //if (MagnetBot.isTestBot)
            System.out.println(WHITE + BLUE_BACKGROUND + log("[DEBUG] " + text) + RESET);
    }

    public static void debug(Object text){
        //if (MagnetBot.isTestBot)
            System.out.println(WHITE + BLUE_BACKGROUND + log("[DEBUG] " + text) + RESET);
    }

    public static void shutdown(int status){
        info("Shutting down");
        Runtime.getRuntime().exit(status);
    }

    private static String log(String s){
        if (file != null) {
            try {
                FileUtils.write(file, s + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

}
