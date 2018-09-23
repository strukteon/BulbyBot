package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 02.09.2018 at 21:44.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.core.sql.UserSQL;
import me.strukteon.bulbybot.core.sql.inventory.InventoryItem;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class Gift implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        Register.isRegistered(event, author, userSQL -> {
            User user = syntax.getAsUser("user");
            EmbedBuilder response = ChatTools.INFO(author);
            InventorySQL inventorySQL = InventorySQL.fromUser(author);
            List<InventoryItem> available = inventorySQL.getAvailableItems().stream().filter(ii -> ii.getItem().getName().toLowerCase().contains(syntax.getAsJoinedListString("item").toLowerCase())).collect(Collectors.toList());
            ChatTools.getUniqueItemFromList(syntax.getAsJoinedListString("item"), response,
                    available, "your inventory", inventoryItem -> {
                if (UserSQL.fromUser(user) != null){
                    Item item = inventoryItem.getItem();
                    inventorySQL.changeOwnership(inventoryItem.getInventoryId(), user.getId());
                    response.setDescription(String.format("You gifted 1x *%s* to **%s**#%s.", item.getName(), user.getName(), user.getDiscriminator()));
                    user.openPrivateChannel().queue(pc -> pc.sendMessage(
                            ChatTools.INFO(user)
                            .setDescription(String.format("You received 1x *%s* from **%s**#%s.", item.getName(), author.getName(), author.getDiscriminator())).build()
                    ).queue());
                } else {
                    response.setDescription(Register.getRegisterMessage(event.getUsedPrefix(), false));
                }
            });
            channel.sendMessage(response.build()).queue();
        });
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("gift")
                .setHelp("gift an item to another user")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                                .addElement("user", SyntaxElementType.USER)
                                .addElement("item", SyntaxElementType.STRING, true)
                );
    }
}
