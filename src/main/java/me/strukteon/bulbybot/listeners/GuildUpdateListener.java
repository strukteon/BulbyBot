package me.strukteon.bulbybot.listeners;
/*
    Created by nils on 31.08.2018 at 19:40.
    
    (c) nils 2018
*/

import com.google.gson.JsonObject;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class GuildUpdateListener extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        postStats(event.getJDA());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        postStats(event.getJDA());
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        postStats(event.getJDA());
    }

    public static void postStats(JDA jda){
        OkHttpClient client = new OkHttpClient();
        try {
            URL dblUrl = new URL(String.format("https://discordbots.org/api/bots/%s/stats", jda.getSelfUser().getId()));
            client.newCall(new Request.Builder().url(dblUrl)
                    .post(RequestBody.create(MediaType.parse("application/json"),
                            new JSONObject()
                            .put("server_count", jda.getGuilds().size())
                            .put("shard_id", jda.getShardInfo().getShardId())
                            .put("shard_count", jda.getShardInfo().getShardTotal())
                    .toString())).build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
