package me.strukteon.bulbybot.commands.moderation;
/*
    Created by nils on 31.08.2018 at 00:20.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.command.ExtendedGuildCommand;
import me.strukteon.bettercommand.tools.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.core.sql.GuildSQL;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class Autorole extends ListenerAdapter implements ExtendedGuildCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, Member author, TextChannel channel) throws Exception {
        GuildSQL guildSQL = GuildSQL.fromGuild(event.getGuild());
        EmbedBuilder response = ChatTools.INFO(author);

        if (syntax.getExecutedBuilder() == 0 || syntax.getExecutedBuilder() == 1){
            List<Role> roles = syntax.getAsRoleList("roles");
            for (Role r : roles)
                if (r.getPosition() > event.getGuild().getMember(event.getJDA().getSelfUser()).getRoles().get(0).getPosition()){
                    response.setDescription(":x: Sorry, but my highest role has to be above all autoroles.");
                    channel.sendMessage(response.build()).queue();
                    return;
                }

            List<Role> savedRoles = guildSQL.getAutoRoles();
            String mode = syntax.getAsSubCommand("mode").getContent();
            StringBuilder successRoles = new StringBuilder();

            if (mode.equals("add")){
                roles.removeAll(savedRoles);
                savedRoles.addAll(roles);
                for (Role r : roles){
                    if (successRoles.length() > 0)
                        successRoles.append(", ");
                    successRoles.append(r.getAsMention());
                }
                if (successRoles.length() == 0)
                    successRoles.append("*None*");
                guildSQL.setAutoRoles(savedRoles);
                response.setDescription(":white_check_mark: Successfully added the following roles to the autoroles: " + successRoles.toString());
            } else {
                roles.removeIf(r -> !savedRoles.contains(r));
                savedRoles.removeAll(roles);
                for (Role r : roles){
                    if (successRoles.length() > 0)
                        successRoles.append(", ");
                    successRoles.append(r.getAsMention());
                }
                if (successRoles.length() == 0)
                    successRoles.append("*None*");
                guildSQL.setAutoRoles(savedRoles);
                response.setDescription(":white_check_mark: Successfully removed the following roles from the autoroles: " + successRoles.toString());
            }
        } else {
            guildSQL.setAutoRoles();
            response.setDescription(":white_check_mark: Successfully disabled autoroling");
        }
        channel.sendMessage(response.build()).queue();

    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        GuildSQL guildSQL = GuildSQL.fromGuild(event.getGuild());
        List<Role> autoroles = guildSQL.getAutoRoles();
        if (autoroles.size() > 0)
            event.getGuild().getController().addRolesToMember(event.getMember(), autoroles).queue();
    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .addRequiredUserPerms(Permission.MANAGE_ROLES)
                .addRequiredBotPerms(Permission.MANAGE_ROLES);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("autorole")
                .setHelp("Automatically give users roles when they join the guild!")
                .setSyntaxBuilder(
                        new SyntaxBuilder(
                        new SyntaxBuilder()
                                .addSubcommand("mode", "add")
                                .addElement("roles", SyntaxElementType.ROLE, true),
                        new SyntaxBuilder()
                                .addSubcommand("mode", "remove")
                                .addElement("roles", SyntaxElementType.ROLE, true),
                        new SyntaxBuilder()
                                .addSubcommand("disable", "disable", "off")

                ));
    }
}
