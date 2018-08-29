package me.strukteon.bettercommand.command;
/*
    Created by nils on 31.07.2018 at 22:50.
    
    (c) nils 2018
*/

public class Loader {

    public interface Prefix {
        String getGuildPrefix(long guildId);
    }

    public interface Blacklisted {
        boolean isUserBlacklisted(long userId);

        boolean isChannelBlacklisted(long channelId);
    }

}
