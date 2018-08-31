package me.strukteon.bulbybot.core;
/*
    Created by nils on 22.08.2018 at 21:07.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.BetterCommand;
import me.strukteon.bulbybot.commands.dev.AddItem;
import me.strukteon.bulbybot.commands.dev.AddMoney;
import me.strukteon.bulbybot.commands.dev.Pull;
import me.strukteon.bulbybot.commands.dev.ThrowException;
import me.strukteon.bulbybot.commands.general.*;
import me.strukteon.bulbybot.commands.money.*;
import me.strukteon.bulbybot.listeners.ReactionAddListener;
import me.strukteon.bulbybot.listeners.ReadyListener;
import me.strukteon.bulbybot.listeners.XPListener;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;

public class Importer {

    public static void importCommands(BetterCommand betterCommand){
        betterCommand
                .addCommandSection("General",
                        new About(),
                        new Execute(),
                        new Settings(),
                        new Stats(),
                        new Invite())
                .addCommandSection("Money",
                        new Balance(),
                        new Daily(),
                        new ItemInfo(),
                        new Inventory(),
                        new Profile(),
                        new Register(),
                        new Market(),
                        new Shop())
                .addCommandSection("Devs only (don't even try)",
                        new AddItem(),
                        new AddMoney(),
                        new ThrowException())
        ;
    }

    public static void importListeners(DefaultShardManagerBuilder builder){
        builder
                .addEventListeners(
                        new ReadyListener(),
                        new ReactionAddListener(),
                        new XPListener()
                );

    }
}
