/*
 * Created by Elias on 08.09.18 14:27
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.commands.moderation;

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedGuildCommand;
import me.strukteon.bettercommand.command.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class Clear implements ExtendedGuildCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, Member author, TextChannel channel) throws Exception {

        int i = Integer.valueOf(event.getMessage().getContentDisplay().replaceFirst(event.getUsedPrefix() + "clear ", ""));

        if (i <= 1 || i > 100) {

            channel.sendMessage(
                new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("Sorry, but I am only able to clear 2 to 100 messages")
                    .build()
            ).queue();

            return;

        }

        event.getMessage().delete().queue();

        MessageHistory history = new MessageHistory(channel);

        history.retrievePast(i).queue(messages -> {

            messages = messages.stream().filter(message -> message.getCreationTime().isAfter(OffsetDateTime.now().minusWeeks(2))).collect(Collectors.toList());

            if (!messages.isEmpty())
                channel.deleteMessages(messages).queue();

            EmbedBuilder eb = ChatTools.INFO(author)
                    .setDescription("Successfully cleared `"+messages.size()+"` messages.");

            channel.sendMessage(eb.build()).queue(message -> {

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        message.delete().queue();
                    }
                }, 3000);

            });

        });

    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .addRequiredUserPerms(Permission.MESSAGE_MANAGE)
                .addRequiredBotPerms(Permission.MESSAGE_MANAGE);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("clear")
                .setHelp("Clears the given amount of messages.")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                        .addElement("number", SyntaxElementType.INT)
                );
    }

}