package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 28.08.2018 at 20:01.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Static;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Date;

public class Daily implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {

        Register.isRegistered(event, author, userSQL -> {
            EmbedBuilder response = ChatTools.INFO(author);
            Date lastDaily = userSQL.getLastDaily();
            if (lastDaily.before(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))){
                response.setDescription("You got your daily reward! " + Static.DAILY_REWARD + "$ were added to your account.");
                userSQL.setLastDaily(new Date());
                userSQL.addMoney(Static.DAILY_REWARD);
            } else
                response.setDescription("You already got your reward for today! Please wait another **" + millisToDisplayFormat(lastDaily.getTime() + 24 * 60 * 60 * 1000 - System.currentTimeMillis()) + "** until you can get your next reward.");

            channel.sendMessage(response.build()).queue();
        });
    }

    String millisToDisplayFormat(long millis){
        int hours = (int)(millis / 3600000);
        int mins = (int)((millis - hours * 3600000) / 60000);
        return (hours != 0 ? hours + "h, " : "") + mins + "min";
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("daily")
                .setHelp("Get your daily reward");
    }
}
