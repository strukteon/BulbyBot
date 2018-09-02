package me.strukteon.bulbybot.utils;
/*
    Created by nils on 23.08.2018 at 20:12.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.BulbyBot;
import net.dv8tion.jda.core.entities.Emote;

public enum Emotes {
    SPINNER("484055620899831809")
    ;

    private String id;

    Emotes(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Emote getAsEmote(){
        return BulbyBot.getShardManager().getEmoteById(id);
    }
}
