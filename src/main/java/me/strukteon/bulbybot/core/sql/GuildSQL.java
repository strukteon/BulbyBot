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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuildSQL {
    public static MySQL mySQL;
    private static String table = "guilds";
    private static String[] columns = {"id", "autoroles", "prefix", "welcomechannel"};

    private String guildid;

    public static void init(MySQL mySQL){
        GuildSQL.mySQL = mySQL;
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
        return mySQL.SELECT("*", table, "id="+guildid).size() != 0;
    }

    public void create(){
        mySQL.INSERT(table, "`id`, `prefix`, `autoroles`, `welcomechannel`", String.format("%s, '%s', '', ''", guildid, Settings.INSTANCE.prefix));
    }


    public String getPrefix(){
        return mySQL.SELECT("*", table, "id="+guildid).get("prefix");
    }

    public List<Role> getAutoRoles(){
        List<Role> roles = new ArrayList<>();
        String rawString = mySQL.SELECT("*", table, "id="+guildid).get("autoroles");
        if (rawString.trim().equals(""))
            return roles;
        String[] rawRoles = rawString.split(" ");
        Arrays.stream(rawRoles).forEach(s -> {
            roles.add(BulbyBot.getShardManager().getRoleById(s));
        });
        return roles;
    }

    public TextChannel getWelcomeChannel(){
        String welcomechannel = mySQL.SELECT("*", table, "id='"+guildid+"'").get("welcomechannel");
        try {
            return BulbyBot.getShardManager().getTextChannelById(welcomechannel);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isLevelingEnabled(){
        return mySQL.SELECT("*", table, "id='"+guildid+"'").get("leveling").equals("1");
    }


    public void setPrefix(String prefix){
        mySQL.UPDATE(table, "prefix='"+prefix+"'", "id='"+guildid+"'");
    }

    public void setAutoRoles(String... roleIds){
        mySQL.UPDATE(table, "autoroles='"+String.join(" ", roleIds)+"'", "id="+guildid);
    }

    public void setAutoRoles(List<Role> roles){
        setAutoRoles(roles.stream().map(Role::getId).toArray(String[]::new));
    }

    public void setLevelingEnabled(boolean enabled){
        mySQL.UPDATE(table, "leveling=" + (enabled ? 1 : 0), "id="+guildid);
    }

    public void setWelcomeChannel(String textChannelId){
        mySQL.UPDATE(table, "welcomechannel='"+textChannelId+"'", "id='"+guildid+"'");
    }
}
