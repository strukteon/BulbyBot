package me.strukteon.bettercommand.syntax;
/*
    Created by nils on 02.04.2018 at 20:15.
    
    (c) nils 2018
*/

import net.dv8tion.jda.core.entities.*;

public class SyntaxObject {

    private Object content;

    public SyntaxObject(Object content){
        this.content = content;
    }

    public Object get(){
        return content;
    }

    public int getAsInt(){
        return (int) content;
    }

    public String getAsString(){
        return (String) content;
    }

    public User getAsUser(){
        return (User) content;
    }

    public Member getAsMember(){
        return (Member) content;
    }

    public Guild getAsGuild(){
        return (Guild) content;
    }

    public TextChannel getAsTextChannel(){
        return (TextChannel) content;
    }

    public VoiceChannel getAsVoiceChannel(){
        return (VoiceChannel) content;
    }




}
