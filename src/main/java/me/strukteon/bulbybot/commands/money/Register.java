package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 25.08.2018 at 00:01.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.core.sql.UserSQL;
import me.strukteon.bulbybot.core.sql.inventory.InventoryItem;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.function.Consumer;

public class Register implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        UserSQL userSQL = UserSQL.fromUser(author);
        if (userSQL == null){
            userSQL = UserSQL.newUser(author);
            userSQL.create();
            userSQL.addMoney(50);

            InventorySQL inventorySQL = InventorySQL.fromUser(author);
            inventorySQL.addItem(Item.BG_ARCHITECTURE, InventoryItem.Availability.IN_USE);
            inventorySQL.addItem(Item.EMBED_COLOR_GREEN, InventoryItem.Availability.IN_USE);

            EmbedBuilder eb = ChatTools.INFO(author)
                    .setDescription("You now have an account! You got 50 bonus credits to get started!");
            channel.sendMessage(eb.build()).queue();
        } else {
            EmbedBuilder eb = ChatTools.INFO(author)
                    .setDescription("Huh? You are already registered.");
            channel.sendMessage(eb.build()).queue();
        }
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("register")
                .setHelp("Register and get yourself an account");
    }

    public static String getRegisterMessage(String prefix){
        return getRegisterMessage(prefix, true);
    }

    public static String getRegisterMessage(String prefix, boolean isSelf){
        return (isSelf ? "You aren't" : "This user isn't") + " registered yet!" + (isSelf ? "Type ``" + prefix + "register`` to do that!" : "");
    }

    public static void isRegistered(CommandEvent event, User user, Consumer<UserSQL> consumer){
        UserSQL userSQL = UserSQL.fromUser(user);
        if (userSQL != null)
            consumer.accept(userSQL);
        else {
            EmbedBuilder eb = ChatTools.INFO(event.getAuthor())
                    .setDescription(getRegisterMessage(event.getUsedPrefix(), event.getAuthor().getId().equals(user.getId())));
            event.getChannel().sendMessage(eb.build()).queue();
        }
    }
}
