package me.strukteon.bulbybot.commands.general;
/*
    Created by nils on 31.08.2018 at 13:44.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Settings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Repository implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        URL url = new URL("https://api.github.com/repos/strukteon/bulbybot/stats/contributors" + Settings.INSTANCE.githubParams);
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(new Request.Builder().url(url).get().build()).execute();
        String responseString = response.body().string();
        System.out.println(responseString);
        JSONArray stats = new JSONArray(responseString);
        int additionsTotal = 0;
        int deletionsTotal = 0;
        int commitsTotal = 0;
        List<Info> infos = new ArrayList<>();
        for (int i = 0; i < stats.length(); i++){
            JSONObject obj = stats.getJSONObject(i);
            JSONArray weeks = obj.getJSONArray("weeks");
            int a = 0;
            int d = 0;
            int c = 0;

            for (Object o : weeks) {
                a += ((JSONObject) o).getInt("a");
                d += ((JSONObject) o).getInt("d");
                c += ((JSONObject) o).getInt("c");
            }

            Info info = new Info(a, d, c, obj.getJSONObject("author").getString("login"), obj.getJSONObject("author").getString("html_url"));
            additionsTotal += a;
            deletionsTotal += d;
            commitsTotal += c;
            infos.add(info);
        }
        EmbedBuilder eb = ChatTools.INFO(author)
                .setAuthor("Statistics of my repository", "https://github.com/strukteon/bulbybot", event.getJDA().getSelfUser().getEffectiveAvatarUrl());
        for (Info i : infos)
            eb.appendDescription(String.format(
                    "Contributer: **[%s](%s)**\n*Additions:* ``%s`` (%s%%)\n*Deletions:* ``%s`` (%s%%)\n*Commits:* ``%s`` (%s%%)\n\n",
                    i.username, i.url, i.additions, i.additions * 100 / additionsTotal, i.deletions, i.deletions * 100 / deletionsTotal, i.commits, i.commits * 100 / commitsTotal
            ));
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("repository")
                .setAliases("repo");
    }

    private class Info {
        int additions;
        int deletions;
        int commits;
        String username;
        String url;
        private Info(int additions, int deletions, int commits, String username, String url){
            this.additions = additions;
            this.deletions = deletions;
            this.commits = commits;
            this.username = username;
            this.url = url;
        }
    }
}
