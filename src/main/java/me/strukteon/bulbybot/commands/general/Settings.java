package me.strukteon.bulbybot.commands.general;
/*
    Created by nils on 24.08.2018 at 19:29.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.commands.money.Register;
import me.strukteon.bulbybot.core.sql.inventory.InventoryItem;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class Settings implements ExtendedCommand {
    List<String> settings = List.of("bio", "background", "color");
    List<String> settingsAliases = List.of("bio", "bg", "colour");
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        if (syntax.getExecutedBuilder() == 0){
            StringBuilder sb = new StringBuilder();
            for (String s : settings){
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(s);
            }
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .addField("Available settings:", String.format("``%s``", sb.toString()), false);
            channel.sendMessage(eb.build()).queue();
        } else {
            if (settings.contains(syntax.getAsString("setting")) || settingsAliases.contains(syntax.getAsString("setting"))){
                Register.isRegistered(event, author, userSQL -> {
                    InventorySQL inventorySQL = InventorySQL.fromUser(author);

                    EmbedBuilder response = new EmbedBuilder()
                            .setColor(Color.GREEN);
                    String setting = syntax.getAsString("setting");
                    if (setting.equals("bio")) {
                        userSQL.setBio(syntax.getAsJoinedListString("value"));
                        response.setDescription("Your bio was set to: " + syntax.getAsJoinedListString("value"));
                    } else if (setting.equals("background") || setting.equals("bg")) {
                        List<InventoryItem> availableItems = inventorySQL.getAvailableItemsWithType(Item.Type.BACKGROUND);
                        String value = syntax.getAsJoinedListString("value").toLowerCase();
                        ChatTools.getUniqueItemFromList(value, response, availableItems.stream().filter(ii -> ii.getItem().getName().toLowerCase().contains(value)).collect(Collectors.toList())
                                , "your inventory", item -> {
                            if (item.getItemId() == inventorySQL.getUsedItem(Item.Type.BACKGROUND).getItemId())
                                response.setDescription("You already have this item equipped!");
                            else {
                                inventorySQL.setUsedItem(item.getInventoryId(), item.getItem().getItemType());
                                response.setDescription("You now have ``" + item.getItem().getName() + "`` equipped as your background!");
                            }
                        });
                    } else if (setting.equals("color") || setting.equals("colour")) {
                        List<InventoryItem> availableItems = inventorySQL.getItemsWithType(Item.Type.EMBED_COLOR);
                        String value = syntax.getAsJoinedListString("value").toLowerCase();
                        ChatTools.getItemFromList(value, response, availableItems.stream().filter(ii -> ii.getItem().getName().toLowerCase().contains(value)).collect(Collectors.toList())
                                , "your inventory", item -> {
                            if (item.getItemId() == inventorySQL.getUsedItem(Item.Type.EMBED_COLOR).getItemId())
                                response.setDescription("You already have this item equipped!");
                            else {
                                System.out.println("Inv ID: " + item.getInventoryId());
                                inventorySQL.setUsedItem(item.getInventoryId(), item.getItem().getItemType());
                                response.setDescription("You now have ``" + item.getItem().getName() + "`` equipped as your embed color!");
                            }
                        });
                    }
                    channel.sendMessage(response.build()).queue();

                });
            } else {
                EmbedBuilder eb = ChatTools.ERROR()
                        .setDescription("This setting does not exist");
                channel.sendMessage(eb.build()).queue();
            }
        }
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("settings")
                .setHelp("Change some of your settings, for example your background image")
                .setAliases("set")
                .setSyntaxBuilder(
                        new SyntaxBuilder(
                                new SyntaxBuilder()
                                    .addSubcommand("list", "list"),

                                new SyntaxBuilder()
                                    .addElement("setting", SyntaxElementType.STRING)
                                    .addElement("value", SyntaxElementType.STRING, true)
                        )
                );
    }
}
