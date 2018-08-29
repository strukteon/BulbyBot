package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 25.08.2018 at 17:50.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.core.sql.inventory.InventoryItem;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;

public class Inventory implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        User user = author;
        if (syntax.getAsMember("user") != null)
            user = syntax.getAsMember("user").getUser();
        User finalUser = user;

        Register.isRegistered(event, user, userSQL -> {
            EmbedBuilder eb = ChatTools.INFO(author)
                    .setTitle(finalUser.getName() + "'s inventory");
            InventorySQL inventorySQL = InventorySQL.fromUser(finalUser);
            HashMap<String, Info> items = new HashMap<>();
            for (InventoryItem item : inventorySQL.getItems()){
                String key = item.getItemId() + item.getAvailability().name();
                if (items.containsKey(key))
                    items.get(key).addOne();
                else
                    items.put(key, new Info(item.getItem(), item.getAvailability()));
            }
            for (Info info : items.values()){
                eb.appendDescription(String.format("\n%s **%s** *x%s* %s", info.item.getEmoteMention(), info.item.getName(), info.amount,
                        !info.availability.equals(InventoryItem.Availability.AVAILABLE) ? "``" + info.availability.getName() + "``" : ""));
            }

            channel.sendMessage(eb.build()).queue();
        });
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("inventory")
                .setHelp("Shows your/an user's inventory")
                .setAliases("inv")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                                .addOptionalElement("user", SyntaxElementType.MEMBER)
                );
    }

    class Info {
        Item item;
        int amount = 1;
        InventoryItem.Availability availability;

        public Info(Item item, InventoryItem.Availability availability){
            this.item = item;
            this.availability = availability;
        }

        public void addOne(){
            amount++;
        }

        Item getItem() {
            return item;
        }

        int getAmount() {
            return amount;
        }

        InventoryItem.Availability getAvailability() {
            return availability;
        }
    }
}
