/*
 * Created by Elias on 01.09.18 11:32
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.utils;

import me.strukteon.bettercommand.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class PermissionsCheck {

    public static boolean hasPermission(CommandEvent event, Member member, Permission permission) {

        if (member.hasPermission(permission))
            return true;


        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Static.COLOR_RED)
                        .setTitle("To low permissions!")
                        .setDescription("You need `"+permission.getName()+"` permissions to use this command!")
                        .build()
        ).queue();

        return false;

    }

}