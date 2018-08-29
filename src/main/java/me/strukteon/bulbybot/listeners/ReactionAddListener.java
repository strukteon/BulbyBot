package me.strukteon.bulbybot.listeners;
/*
    Created by nils on 26.08.2018 at 00:04.
    
    (c) nils 2018
*/

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ReactionAddListener extends ListenerAdapter {
    private static Map<String, Consumer<MessageReactionAddEvent>> internalListeners = new HashMap<>();

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (internalListeners.containsKey(event.getMessageId()))
            internalListeners.get(event.getMessageId()).accept(event);
    }

    public static boolean addConsumer(String messageId, Consumer<MessageReactionAddEvent> consumer){
        boolean success;
        if (success = !internalListeners.containsKey(messageId))
            internalListeners.put(messageId, consumer);
        return success;
    }

    public static void removeConsumer(String messageId){
        internalListeners.remove(messageId);
    }
}
