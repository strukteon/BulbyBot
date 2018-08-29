package me.strukteon.bettercommand.syntax;
/*
    Created by nils on 02.04.2018 at 20:14.
    
    (c) nils 2018
*/

import net.dv8tion.jda.core.entities.*;

import java.util.HashMap;
import java.util.List;

public class Syntax extends HashMap<String, Object> {

    private int executedBuilder = 0;
    private String help;

    protected void setExecutedBuilder(int executedBuilder) {
        this.executedBuilder = executedBuilder;
    }

    protected void setHelp(String help) {
        this.help = help;
    }

    public String getHelp() {
        return help;
    }

    public int getExecutedBuilder() {
        return executedBuilder;
    }

    public int getAsInt(String name){
        return (int) this.get(name);
    }

    public long getAsLong(String name){
        return (long) this.get(name);
    }

    public String getAsString(String name){
        return (String) this.get(name);
    }

    public User getAsUser(String name){
        return (User) this.get(name);
    }

    public Member getAsMember(String name){
        return (Member) this.get(name);
    }

    public Guild getAsGuild(String name){
        return (Guild) this.get(name);
    }

    public TextChannel getAsTextChannel(String name){
        return (TextChannel) this.get(name);
    }

    public VoiceChannel getAsVoiceChannel(String name){
        return (VoiceChannel) this.get(name);
    }

    public Role getAsRole(String name){
        return (Role) this.get(name);
    }

    public List<Integer> getAsListInt(String name){
        return (List<Integer>) this.get(name);
    }

    public List<String> getAsListString(String name){
        return (List<String>) this.get(name);
    }

    public String getAsJoinedListString(String name){
        System.out.println(getAsListString(name));
        return String.join(" ", getAsListString(name));
    }

    public List<User> getAsListUser(String name){
        return (List<User>) this.get(name);
    }

    public List<Member> getAsListMember(String name){
        return (List<Member>) this.get(name);
    }

    public List<Guild> getAsListGuild(String name){
        return (List<Guild>) this.get(name);
    }

    public List<TextChannel> getAsTextChannelList(String name){
        return (List<TextChannel>) this.get(name);
    }

    public List<VoiceChannel> getAsVoiceChanneList(String name){
        return (List<VoiceChannel>) this.get(name);
    }

    public List<Role> getAsRoleList(String name){
        return (List<Role>) this.get(name);
    }

    public SubCommand getAsSubCommand(String name){
        return (SubCommand) this.get(name);
    }

}
