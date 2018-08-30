package me.strukteon.bulbybot.listeners;
/*
    Created by nils on 22.08.2018 at 20:54.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.BulbyBot;
import me.strukteon.bulbybot.core.CLI;
import me.strukteon.bulbybot.core.sql.MySQL;
import me.strukteon.bulbybot.core.sql.PatreonSQL;
import me.strukteon.bulbybot.core.sql.UserSQL;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.core.sql.market.MarketSQL;
import me.strukteon.bulbybot.core.threading.RenderThread;
import me.strukteon.bulbybot.utils.CoolStatus;
import me.strukteon.bulbybot.utils.MySqlSettings;
import me.strukteon.bulbybot.utils.Settings;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class ReadyListener extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();

        CLI.info("Logged in as " + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator());
        CLI.info(jda.getGuilds().size() + " Guilds");

        int users = 0;
        for (Guild g : jda.getGuilds())
            users += g.getMembers().size();
        CLI.info(+users+ " Users, " + jda.getUsers().size() + " Online (" + Math.round(jda.getUsers().size()/(float)users*100) + "%)");


        MySqlSettings mySqlSettings = Settings.INSTANCE.mySqlSettings;
        MySQL mySQL = new MySQL(mySqlSettings.host, mySqlSettings.port, mySqlSettings.username, mySqlSettings.password, mySqlSettings.database);
        mySQL.connect();
        UserSQL.init(mySQL);
        PatreonSQL.init(mySQL);
        InventorySQL.init(mySQL);
        MarketSQL.init(mySQL);

        BulbyBot.initCommandHandler();

        jda.getPresence().setPresence(OnlineStatus.ONLINE, true);

        BulbyBot.defaultRenderThread = new RenderThread();
        BulbyBot.premiumRenderThread = new RenderThread();

        BulbyBot.defaultRenderThread.start();
        BulbyBot.premiumRenderThread.start();

        new CoolStatus(jda).start();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Connection old = mySQL.getConnection();
                mySQL.connect();
                try {
                    old.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 30 * 60 * 1000, 30 * 60 * 1000);
    }
}
