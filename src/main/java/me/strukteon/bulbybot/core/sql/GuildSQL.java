package me.strukteon.bulbybot.core.sql;
/*
    Created by nils on 30.04.2018 at 19:37.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.BulbyBot;
import me.strukteon.bulbybot.utils.Settings;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.List;

public class GuildSQL {
    public static MySQL mySQL;
    private static String table = "guilds";
    private static String[] columns = {"id", "autorole", "prefix", "savedqueue", "welcomechannel"};

    private String guildid;

    public static void init(MySQL mySQL){
        GuildSQL.mySQL = mySQL;

        if (!mySQL.TABLE_EXISTS(table))
            mySQL.CREATE_TABLE(table, columns);

        mySQL.MATCH_COLUMNS(table, columns);
    }


    private GuildSQL(String guildid){
        this.guildid = guildid;
        if (!exists())
            create();
    }


    public static GuildSQL fromGuildId(String guildid){
        return new GuildSQL(guildid);
    }

    public static GuildSQL fromGuild(Guild guild){
        return fromGuildId(guild.getId());
    }


    public boolean exists(){
        return mySQL.SELECT("*", table, "id='"+guildid+"'").size() != 0;
    }

    public void create(){
        mySQL.INSERT(table, "`id`, `prefix`, `autorole`, `savedqueue`, `welcomechannel`", String.format("'%s', '%s', '', '', ''", guildid, Settings.INSTANCE.prefix));
    }


    public String getPrefix(){
        return mySQL.SELECT("*", table, "id='"+guildid+"'").get("prefix");
    }

    public Role getAutoRole(){
        String autorole = mySQL.SELECT("*", table, "id='"+guildid+"'").get("autorole");
        try {
            return BulbyBot.getShardManager().getRoleById(autorole);
        } catch (IllegalArgumentException e){
            return null;
        }
    }

    public TextChannel getWelcomeChannel(){
        String welcomechannel = mySQL.SELECT("*", table, "id='"+guildid+"'").get("welcomechannel");
        try {
            return BulbyBot.getShardManager().getTextChannelById(welcomechannel);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public List<String> getSavedQueue(){
        String savedqueue = mySQL.SELECT("*", table, "id='"+guildid+"'").get("savedqueue");
        return Arrays.asList(savedqueue.split(" "));
    }


    public void setPrefix(String prefix){
        mySQL.UPDATE(table, "prefix='"+prefix+"'", "id='"+guildid+"'");
    }

    public void setAutoRole(String roleId){
        mySQL.UPDATE(table, "autorole='"+roleId+"'", "id='"+guildid+"'");
    }

    public void setWelcomeChannel(String textChannelId){
        mySQL.UPDATE(table, "welcomechannel='"+textChannelId+"'", "id='"+guildid+"'");
    }

    public void setSavedQueue(List<String> queue){
        StringBuilder b = new StringBuilder();
        queue.forEach(s -> b.append(s + " "));
        mySQL.UPDATE(table, "savedqueue='"+b.toString()+"'", "id='"+guildid+"'");
    }
}
