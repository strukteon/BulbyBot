package me.strukteon.bulbybot.commands.dev;
/*
    Created by nils on 25.08.2018 at 21:09.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.tools.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.commands.money.Register;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Settings;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class AddItem implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        Register.isRegistered(event, syntax.getAsUser("user"), userSQL -> {
            InventorySQL inventorySQL = InventorySQL.fromUser(syntax.getAsUser("user"));
            Item item = Item.getItemById(syntax.getAsInt("itemid"));
            if (item == null){
                EmbedBuilder eb = ChatTools.INFO(author)
                        .setDescription("An item with the id ``" + syntax.getAsInt("itemid") + "`` does not exist");
                channel.sendMessage(eb.build()).queue();
            } else {
                inventorySQL.addItem(item);
                EmbedBuilder eb = ChatTools.INFO(author)
                        .setDescription("The item *" + item.getName() + "* was added to " + syntax.getAsUser("user").getAsMention() + "'s inventory");
                channel.sendMessage(eb.build()).queue();
            }

        });
    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .limitToUsers(Settings.INSTANCE.developers);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("additem")
                .setHelp("Adds an item to an users inventory")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                        .addElement("user", SyntaxElementType.USER)
                        .addElement("itemid", SyntaxElementType.INT)
                );
    }
}
