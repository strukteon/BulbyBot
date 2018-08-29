package me.strukteon.bettercommand;
/*
    Created by nils on 08.08.2018 at 19:16.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.command.BaseCommand;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.command.GuildCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;
import java.awt.*;

public class DefaultHelpCommand implements ExtendedCommand {
    private String label;
    private String[] aliases;
    private Color color;

    protected DefaultHelpCommand(@Nonnull String label, @Nonnull String[] aliases){
        this(label, Color.decode("#7289DA"), aliases);
    }

    protected DefaultHelpCommand(@Nonnull String label, @Nonnull Color color, @Nonnull String[] aliases){
        this.label = label;
        this.aliases = aliases;
        this.color = color;
    }

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) {
        EmbedBuilder builder = //new EmbedBuilder().setColor(color);
                ChatTools.INFO(author);
        String cmd = syntax.getAsString("command");
        if (cmd != null){
            for (BaseCommand bc : event.getBetterCommand().getCommands())
                if (bc.getCommandInfo().getLabel().equals(cmd) || bc.getCommandInfo().getAliases().contains(cmd)){
                    MessageEmbed overridden = bc.setOverridenHelp(event);
                    if (overridden != null) {
                        channel.sendMessage(overridden).queue();
                        return;
                    }
                    builder.setTitle("Infos about " + bc.getCommandInfo().getLabel());
                    StringBuilder additionalInfo = new StringBuilder();

                    if (bc.getCommandInfo().getAliases().size() > 0){
                        StringBuilder aliases = new StringBuilder();
                        for (String alias : bc.getCommandInfo().getAliases()){
                            if (aliases.length() > 0)
                                aliases.append(", ");
                            aliases.append("``" + alias + "``");
                        }
                        builder.addField("Aliases:", aliases.toString(), false);
                    }

                    if (bc instanceof ExtendedCommand) {
                        StringBuilder syntaxb = new StringBuilder();
                        bc.getCommandInfo().getSyntaxBuilder().getAsHelp(cmd);
                        builder.addField("Syntax:", bc.getCommandInfo().getSyntaxBuilder().getAsHelp(cmd), false);
                    } else
                        builder.addField("Syntax:", bc.getCommandInfo().getSyntax(), false);

                    if (event.isFromType(ChannelType.PRIVATE) && bc instanceof GuildCommand)
                        builder.setDescription("\n**Note:** This command is limited to guilds!\n\n");
                    else if (event.isFromType(ChannelType.TEXT))
                        builder.addField("Permissions:",
                                "**__Bot permissions__:**\n" + CommandTools.permsCheck(bc.getPermissionManager().getRequiredBotPerms(), event.getGuild().getMember(event.getJDA().getSelfUser()).getPermissions(event.getTextChannel())) + "\n" +
                                    "**__User permissions__:**\n" + CommandTools.permsCheck(bc.getPermissionManager().getRequiredUserPerms(), event.getMember().getPermissions(event.getTextChannel()))
                                , false);

                    if (bc.getCommandInfo().isNsfwOnly())
                        additionalInfo.append("*Limited to NSFW/private channels.");

                    if (additionalInfo.length() > 0)
                        builder.addField("Additional info:", additionalInfo.toString(), false);

                    builder.appendDescription(bc.getCommandInfo().getHelp());

                    channel.sendMessage(builder.build()).queue();
                    return;
                }
        } else {
            builder.setDescription("Type ``" + event.getUsedPrefix() + label + " <command>`` for infos about a specific command");

            StringBuilder unassigned = new StringBuilder();
            for (BaseCommand bc : event.getBetterCommand().getUnassignedCommands())
                unassigned.append("``" + bc.getCommandInfo().getLabel() + "``, ");

            unassigned.delete(unassigned.length() - 2, unassigned.length());
            builder.addField("**Commands:**", unassigned.toString(), false);

            for (CommandSection cs : event.getBetterCommand().getCommandSections()) {
                if (cs.getCommands().size() > 0) {
                    StringBuilder sec = new StringBuilder();
                    for (BaseCommand bc : cs.getCommands())
                        sec.append("``" + bc.getCommandInfo().getLabel() + "``, ");

                    sec.delete(sec.length() - 2, sec.length());
                    builder.addField(cs.getName(), sec.toString(), false);
                }
            }
            builder.addField("", "Loaded a total of ``" + event.getBetterCommand().getCommands().size() + "`` Commands.", false);

            channel.sendMessage(builder.build()).queue();
        }
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo(label)
                .setHelp("list all available commands")
                .setAliases(aliases)
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                            .addOptionalElement("command", SyntaxElementType.STRING)
                );
    }
}
