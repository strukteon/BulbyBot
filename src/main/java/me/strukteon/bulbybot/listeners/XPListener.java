package me.strukteon.bulbybot.listeners;
/*
    Created by nils on 28.08.2018 at 01:50.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.core.sql.UserSQL;
import me.strukteon.bulbybot.utils.LevelSystem;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class XPListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (UserSQL.existsFromUser(event.getAuthor()))
            UserSQL.fromUser(event.getAuthor()).addXp(LevelSystem.getXp(event.getMessage().getContentRaw().length()));
    }
}
