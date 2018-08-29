package me.strukteon.bettercommand;
/*
    Created by nils on 08.08.2018 at 15:27.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.command.Loader;

public class DefaultLoader {

    public static class Prefix implements Loader.Prefix {

        @Override
        public String getGuildPrefix(long guildId) {
            return null;
        }
    }

    public static class Blacklisted implements Loader.Blacklisted {
        @Override
        public boolean isUserBlacklisted(long userId) {
            return false;
        }

        @Override
        public boolean isChannelBlacklisted(long channelId) {
            return false;
        }
    }
}
