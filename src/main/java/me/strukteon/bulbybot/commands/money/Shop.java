package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 25.08.2018 at 15:35.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Static;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Shop implements ExtendedCommand {
    private static List<Item> qualifiedItems = Arrays.stream(Item.values()).filter(item -> item.getPrice() > 0).collect(Collectors.toList());

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        Register.isRegistered(event, author, userSQL -> {
            EmbedBuilder eb = ChatTools.INFO(author);
            InventorySQL inventorySQL = InventorySQL.fromUser(author);

            if (syntax.getExecutedBuilder() == 2){
                shopBuilder(event, eb, 0, qualifiedItems.size());
                channel.sendMessage(eb.build()).queue();
            } else {
                Item item = null;
                if (syntax.getExecutedBuilder() == 0){
                    item = Item.getItemById(syntax.getAsInt("itemid"));
                    if (item == null) {
                        eb.setDescription("Sorry, but no item with the id ``" + syntax.getAsInt("itemid") + "`` exists.");
                    }
                } else {
                    List<Item> items = Item.getItemsByName(syntax.getAsJoinedListString("item"));
                    if (items.size() == 0) {
                        eb.setDescription("Sorry, but no item with the name ``" + syntax.getAsJoinedListString("itemname") + "`` is available in the shop.");
                    } else if (items.size() > 1) {
                        eb.setDescription("I found more than one item containing this text. Please try it again with a more detailed name.");
                        for (Item i : items)
                            eb.appendDescription("\n*" + i.getName() + "*");
                    } else
                        item = items.get(0);
                }
                if (item != null)
                    if (userSQL.getMoney() < item.getPrice()){
                        eb.setDescription("You don't have enough money to purchase this! Missing amount: ``" + (item.getPrice() - userSQL.getMoney()) + "``. Type ``" + event.getUsedPrefix() + "howto get money`` to get some options listed how you can acquire money.");
                    } else {
                        userSQL.addMoney(-item.getPrice());
                        inventorySQL.addItem(item);
                        eb.setDescription("Thank you for purchasing 1x ``" + item.getName() + "``. Visit us again soon!")
                                .setFooter("You have " + userSQL.getMoney() + "$ left", author.getEffectiveAvatarUrl());
                    }

                channel.sendMessage(eb.build()).queue();
            }
        });
    }

    private EmbedBuilder shopBuilder(CommandEvent event, EmbedBuilder eb, int start, int end){
        eb
                .setAuthor(event.getAuthor().getName() + " visits the shop", Static.PLACEHOLDER_URL, event.getAuthor().getEffectiveAvatarUrl())
                .setDescription("To get more information about an item, type ``" + event.getUsedPrefix() + "iteminfo <itemId>``");

        List<Item> qualified = qualifiedItems.subList(start, end);
        for (Item.Type it : Item.Type.values()){
            StringBuilder sb = new StringBuilder();
            qualified.stream().filter(item -> item.getItemType().equals(it)).collect(Collectors.toList()).forEach(
                    item -> sb.append(sb.length() > 0 ? "\n" : "")
                    .append(String.format("%s **ID ``%s``:** *%s*; Price: **%s$**", item.getEmoteMention(), item.getId(), item.getName(), item.getPrice()))
            );
            if (sb.length() > 0)
                eb.addField(it.getName(), sb.toString(), false);
        }
        return eb;
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("shop")
                .setHelp("Give us all your money [insert-lennyface-here]")
                .setAliases("store")
                .setSyntaxBuilder(new SyntaxBuilder(
                        new SyntaxBuilder()
                            .addSubcommand("mode", "buy")
                            .addElement("itemid", SyntaxElementType.INT),
                        new SyntaxBuilder()
                            .addSubcommand("mode", "buy")
                            .addElement("item", SyntaxElementType.STRING, true),
                        new SyntaxBuilder()
                ));
    }
}
