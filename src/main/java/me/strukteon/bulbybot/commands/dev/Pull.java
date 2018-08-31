/*
 * Created by Elias on 31.08.18 08:55
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.commands.dev;

        import me.strukteon.bettercommand.CommandEvent;
        import me.strukteon.bettercommand.command.CommandInfo;
        import me.strukteon.bettercommand.command.ExtendedCommand;
        import me.strukteon.bettercommand.syntax.Syntax;
        import net.dv8tion.jda.core.entities.MessageChannel;
        import net.dv8tion.jda.core.entities.User;

public class Pull implements ExtendedCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {

        event.getTextChannel().sendMessage("Pulling started!").queue();
        Runtime.getRuntime().exec("bash /home/bulby.sh");

    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("pull")
                .setHelp("Pulls the newest version from github!");
    }

}
