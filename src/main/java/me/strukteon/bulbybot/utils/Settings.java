package me.strukteon.bulbybot.utils;
/*
    Created by nils on 22.08.2018 at 20:50.
    
    (c) nils 2018
*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Settings {
    public static Settings INSTANCE = new Settings();

    public String token = "";
    public String prefix = "b!";

    public List<Integer> shardIds = List.of(0);
    public int shardCount = 1;

    public String errorChannelId = "482637061279449098";

    public String[] developers = {"262951897290244096"};

    public MySqlSettings mySqlSettings = new MySqlSettings();

    public String jDoodleClientId = "";
    public String jDoodleClientSecret = "";

    public String dblToken = "";

    public static void loadFromFile(File file) throws FileNotFoundException {
        Gson gson = new Gson();
        INSTANCE = gson.fromJson(new FileReader(file), Settings.class);
    }

    public static void createNewFile(File file) throws IOException {
        if (!file.exists())
            file.createNewFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileUtils.write(file, gson.toJson(new Settings(), Settings.class));
    }

}
