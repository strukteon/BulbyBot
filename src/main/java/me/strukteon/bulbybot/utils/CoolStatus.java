package me.strukteon.bulbybot.utils;
/*
    Created by nils on 05.04.2018 at 19:41.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.BulbyBot;
import me.strukteon.bulbybot.core.sql.UserSQL;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class CoolStatus extends Thread {
    private Timer timer;
    private int cur = 0;
    private long period;

    private JDA jda;

    private StatusMessage[] msgs = {
            s -> "Working on it!",
            s -> "inv.bulby.xyz",
            s -> "donate.bulby.xyz",
            s -> "Much wow",
            s -> "Fun included!",
            s -> "It's a feature, not a bug!",
            s -> "Hunting bugs",
            s -> "Generating Bitcoins",
            s -> s.getGuilds().size() + " Guilds",
            s -> UserSQL.getTotalUserSize() + " Registered Users",
            s -> s.getUsers().size() + " Total users",
            s -> "What do I do again?",
            s -> "beep boop",
            s -> "Is this thing on?",
            s -> "Collecting taxes"
    };

    public CoolStatus(JDA jda){
        this(jda, 30000);
    }

    public CoolStatus(JDA jda, long period){
        this.timer = new Timer();
        this.period = period;
        this.jda = jda;
    }

    public void start(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 60000/5);
    }

    private void update(){
        String msg = msgs[cur].generate(BulbyBot.getShardManager());
        jda.getPresence().setPresence(Game.playing(Settings.INSTANCE.prefix + "help | " + msg), false);
        cur++;
        if (cur == msgs.length)
            cur = 0;
    }

    public static class OnlineState {
        int status = 0;
        public OnlineStatus switch_(){
            switch (status){
                case 0:
                    status++;
                    return OnlineStatus.ONLINE;
                case 1:
                    status++;
                    return OnlineStatus.DO_NOT_DISTURB;
                case 2:
                    status = 0;
                    return OnlineStatus.IDLE;
            }
            return OnlineStatus.ONLINE;
        }
    }

    private interface StatusMessage {
        String generate(ShardManager shardManager);
    }

}
