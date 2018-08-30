package me.strukteon.bulbybot;
/*
    Created by nils on 22.08.2018 at 20:47.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.BetterCommand;
import me.strukteon.bulbybot.core.CLI;
import me.strukteon.bulbybot.core.Importer;
import me.strukteon.bulbybot.core.threading.RenderThread;
import me.strukteon.bulbybot.utils.Settings;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BulbyBot {

    public static ShardManager shardManager;
    public static BetterCommand betterCommand;

    public static RenderThread defaultRenderThread;
    public static RenderThread premiumRenderThread;

    private static long startTime;

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        File settings = new File("config.json");

        try {
            if (!settings.exists()) {
                CLI.error("The config does not exist... Please edit config.json to set your token etc (" + settings.getAbsolutePath() + ")");
                Settings.createNewFile(settings);
                System.exit(1);
            } else {
                Settings.loadFromFile(settings);
                CLI.info("Loaded config");
            }
        } catch (IOException e) {
            CLI.error("An error occurred while trying to load the config: " + e.getMessage());
        }

        CLI.info("Starting...\n ______     __  __     __         ______     __  __    \n" +
                "/\\  == \\   /\\ \\/\\ \\   /\\ \\       /\\  == \\   /\\ \\_\\ \\   \n" +
                "\\ \\  __<   \\ \\ \\_\\ \\  \\ \\ \\____  \\ \\  __<   \\ \\____ \\  \n" +
                " \\ \\_____\\  \\ \\_____\\  \\ \\_____\\  \\ \\_____\\  \\/\\_____\\ \n" +
                "  \\/_____/   \\/_____/   \\/_____/   \\/_____/   \\/_____/");

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
        builder
                .setToken(Settings.INSTANCE.token)
                .setShardsTotal(Settings.INSTANCE.shardCount)
                .setShards(Settings.INSTANCE.shardIds)
                .setGame(Game.playing("they see me loading"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB);

        Importer.importListeners(builder);
        BulbyBot.initCommandHandler();
        
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void initCommandHandler(){
        betterCommand = new BetterCommand(Settings.INSTANCE.prefix, true);
        Importer.importCommands(betterCommand);
        betterCommand
                .useDefaultHelpMessage("help", Color.decode("#7BFF2E"), "?")
                .useShardManager(shardManager)
                .setCooldown(200)
                .enable();
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public static long getStartTime() {
        return startTime;
    }
}
