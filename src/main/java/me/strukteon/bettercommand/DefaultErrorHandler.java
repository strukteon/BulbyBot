package me.strukteon.bettercommand;
/*
    Created by nils on 24.08.2018 at 20:45.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.command.BaseCommand;
import me.strukteon.bettercommand.command.ErrorHandler;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxValidateException;
import me.strukteon.bulbybot.BulbyBot;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Settings;
import me.strukteon.bulbybot.utils.Static;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.List;

public class DefaultErrorHandler implements ErrorHandler {

    @Override
    public void missingBotPermissions(CommandEvent event, List<Permission> missingPermissions, BaseCommand command) {
        EmbedBuilder eb = ChatTools.ERROR()
                .setTitle("Missing Permissions")
                .setDescription("Whoops, it seems like " + event.getJDA().getSelfUser().getAsMention() + " is missing some permissions! Please permit them to use this command: ``");
        for (Permission p : missingPermissions)
            eb.getDescriptionBuilder().append(eb.getDescriptionBuilder().length() > 0 ? ", " : "").append(p.getName());
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void missingUserPermissions(CommandEvent event, List<Permission> missingPermissions, BaseCommand command) {
        EmbedBuilder eb = ChatTools.ERROR()
                .setTitle("Missing Permissions")
                .setDescription("Whoops, it seems like you (" + event.getAuthor().getAsMention() + ") are missing some permissions! You need the following perms to execute this command: ``");
        for (Permission p : missingPermissions)
            eb.getDescriptionBuilder().append(eb.getDescriptionBuilder().length() > 0 ? ", " : "").append(p.getName());
        eb.appendDescription("``");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void notInUserlist(CommandEvent event, BaseCommand command) {
        EmbedBuilder eb = ChatTools.ERROR()
                .setTitle("Missing Permissions")
                .setDescription("Sorry, but this command is limited to the developers.");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public boolean validateException(CommandEvent event, BaseCommand cmd, SyntaxValidateException e) {
        EmbedBuilder eb = ChatTools.ERROR()
                .setTitle("Wrong syntax!");
        for (SyntaxBuilder sb : cmd.getCommandInfo().getSyntaxBuilder().getAlternateBuilders())
            eb.getDescriptionBuilder().append(eb.getDescriptionBuilder().length() > 0 ? "\n" : "").append(
                    "**" + event.getUsedPrefix() + cmd.getCommandInfo().getLabel() + "** " + sb.toString()
            );
        event.getChannel().sendMessage(eb.build()).queue();
        return false;
    }

    @Override
    public void onException(CommandEvent event, BaseCommand cmd, Exception e) {
        EmbedBuilder eb = ChatTools.ERROR()
                .setDescription(String.format("Oh no, it seems like an error ocurred:\n```css\n%s\n```\n\nAn error report was sent to the developers.", e))
                .setFooter(String.format("@%s#%s:%s", e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getMethodName(), e.getStackTrace()[0].getLineNumber()), event.getJDA().getSelfUser().getEffectiveAvatarUrl());
        BulbyBot.getShardManager().getTextChannelById(Settings.INSTANCE.errorChannelId).sendMessage(createExceptionMessage(event, cmd, e)).queue();
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private MessageEmbed createExceptionMessage(CommandEvent event, BaseCommand cmd, Exception e){
        e.printStackTrace();
        EmbedBuilder eb = ChatTools.ERROR();
        String username = event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(),
                userId = event.getAuthor().getId(),
                guildName = event.isGuild() ? event.getGuild().getName() : "[ None ]",
                guildId = event.isGuild() ? event.getGuild().getId() : "[ None ]";
        StringBuilder stackTrace = new StringBuilder();
        for (int i = 0; i < Math.min(e.getStackTrace().length, 5); i++)
            stackTrace.append(stackTrace.length() > 0 ? "\n" : "").append(e.getStackTrace()[i]);
        if (cmd != null)
            eb.setDescription("Command: " + cmd.getCommandInfo().getLabel());
        eb
                .setColor(Static.COLOR_RED)
                .addField("Info:", String.format("**Guild:** *%s* ``%s``\n**User:** *%s* ``%s``", guildName, guildId, username, userId), false)
                .addField("Error Message:", "```css\n" + e + "\n```", false)
                .addField("StackTrace:", stackTrace.toString(), false);

        return eb.build();
    }

}
