package me.strukteon.bulbybot.listeners;
/*
    Created by nils on 28.08.2018 at 01:50.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.core.sql.UserSQL;
import me.strukteon.bulbybot.utils.LevelSystem;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class XPListener extends ListenerAdapter {
    private static Map<String, Long> cooldown = new HashMap<>();
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (UserSQL.existsFromUser(author)) {
            UserSQL userSQL = UserSQL.fromUser(author);
            userSQL.addXp(LevelSystem.getXp(event.getMessage().getContentRaw().length()));
            if (!cooldown.containsKey(author.getId()) || cooldown.get(author.getId()) < System.currentTimeMillis() - 10000) {
                cooldown.replace(author.getId(), System.currentTimeMillis());
                userSQL.addMoney(1);
            }
        }
    }
}
