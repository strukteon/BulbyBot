package me.strukteon.bulbybot.utils;
/*
    Created by nils on 23.08.2018 at 00:28.
    
    (c) nils 2018
*/

import java.awt.*;
import java.text.SimpleDateFormat;

public class Static {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static final String PLACEHOLDER_URL = "https://strukteon.me";


    public static final Color COLOR_RED = Color.decode("#cc2222");
    public static final Color COLOR_GREEN = Color.GREEN;


    public static final int RENDER_QUEUE_CAPACITY = 1;


    public static final int DAILY_REWARD = 30;

    public static class Files {
        public static String IMAGES_DIR = "/images/";

        public static String PROFILE_BG_DIR = IMAGES_DIR + "backgrounds/";
        public static String PROFILE_BADGES_DIR = IMAGES_DIR + "badges/";

    }
}
