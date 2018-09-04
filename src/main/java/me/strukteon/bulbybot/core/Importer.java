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
import me.strukteon.bulbybot.commands.moderation.*;
import me.strukteon.bulbybot.commands.money.*;
import me.strukteon.bulbybot.listeners.*;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;

public class Importer {

    public static void importCommands(BetterCommand betterCommand){
        betterCommand
                .addCommandSection("Moderation",
                        new Autorole(),
                        new Prefix(),
                        new Ban(),
                        new Kick(),
                        new Autochannel(),
                        new Leveling())

                .addCommandSection("General",
                        new About(),
                        new Execute(),
                        new Settings(),
                        new Stats(),
                        new Invite(),
                        new Repository())

                .addCommandSection("Money",
                        new Balance(),
                        new Daily(),
                        new Gift(),
                        new Pay(),
                        new ItemInfo(),
                        new Inventory(),
                        new Profile(),
                        new Redeem(),
                        new Register(),
                        new Market(),
                        new Shop(),
                        new Vote())

                .addCommandSection("Devs only (don't even try)",
                        new AddItem(),
                        new AddMoney(),
                        new ThrowException(),
                        new Pull())
        ;
    }

    public static void importListeners(DefaultShardManagerBuilder builder){
        builder
                .addEventListeners(
                        new GuildUpdateListener(),
                        new ReadyListener(),
                        new ReactionAddListener(),
                        new XPListener(),
                        new Autorole(),
                        new MentionListener(),
                        new AutochannelListener()
                );

    }
}
