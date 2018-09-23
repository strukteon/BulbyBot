package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 26.08.2018 at 21:26.
    
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
import me.strukteon.bulbybot.core.sql.market.MarketEntry;
import me.strukteon.bulbybot.core.sql.market.MarketSQL;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Settings;
import me.strukteon.bulbybot.utils.Static;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Market implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        Register.isRegistered(event, author, userSQL -> {
            EmbedBuilder response = ChatTools.INFO(author);
            String mode = syntax.getAsSubCommand("mode").getContent();
            InventorySQL inventorySQL = InventorySQL.fromUser(author);

            if (mode.equals("sell")){
                List<InventoryItem> availableItems = inventorySQL.getAvailableItems();
                MarketEntry marketEntry = null;
                if (syntax.getExecutedBuilder() == 0){
                    for (InventoryItem i : availableItems)
                        if (syntax.getAsInt("price") < 0)
                            break;
                        else if (i.getItemId() == syntax.getAsInt("itemid")){
                            marketEntry = MarketSQL.addEntry(i, syntax.getAsInt("price"));
                            break;
                        }
                    if (syntax.getAsInt("price") < 0) {
                            response.setDescription("You can't sell something with a negative price!");
                    } else if (marketEntry == null){
                        response.setDescription("Sorry, but you don't have an available item with the id ``" + syntax.getAsInt("itemid") + "`` in your inventory.");
                    } else {
                        response.setDescription("Your item is now for sale on the market!\n**Item:** *" + marketEntry.getItem().getName() + "*\n**Market ID:** *``" + marketEntry.getMarketId() + "``*\n**Price:** *" + marketEntry.getPriceDisplay() + "*");
                    }
                } else {
                    Map<Integer, InventoryItem> uniqueAvailableItems = new HashMap<>();
                    availableItems.forEach(ii -> {
                        if (!uniqueAvailableItems.containsKey(ii.getItemId()))
                            uniqueAvailableItems.put(ii.getItemId(), ii);
                    });

                    List<InventoryItem> items = uniqueAvailableItems.values().stream().filter(ii -> ii.getItem().getName().toLowerCase().contains(syntax.getAsJoinedListString("item").toLowerCase())).collect(Collectors.toList());
                    if (items.size() == 0) {
                        response.setDescription("Sorry, but no item with the name ``" + syntax.getAsJoinedListString("item") + "`` is available in your inventory.");
                    } else if (items.size() > 1) {
                        response.setDescription("I found more than one item containing this text. Please try it again with a more detailed name.");
                        for (InventoryItem i : items)
                            response.appendDescription("\n*" + i.toString() + "*");
                    } else
                        marketEntry = MarketSQL.addEntry(items.get(0), syntax.getAsInt("price"));
                    ;

                    if (syntax.getAsInt("price") < 0) {
                        response.setDescription("You can't sell something with a negative price!");
                    } else {
                        response.setDescription("Your item is now for sale on the market!\n**Item:** *" + marketEntry.getItem().getName() + "*\n**Market ID:** *``" + marketEntry.getMarketId() + "``*\n**Price:** *" + marketEntry.getPriceDisplay() + "*");
                    }
                }
            } else if (mode.equals("search")){

                if (syntax.getExecutedBuilder() == 4){
                    Item item = Item.getItemById(syntax.getAsInt("itemid"));
                    if (item == null)
                        response.setDescription("Sorry, but no item with the id ``" + syntax.getAsInt("itemid") + "`` exists.");
                    else {
                        List<MarketEntry> marketEntries = MarketSQL.searchFor(item);
                        marketBuilder(event, marketEntries, response, 0, Math.min(10, marketEntries.size()));
                    }
                } else {
                    List<Item> items = Item.getItemsByName(syntax.getAsJoinedListString("itemname"));
                    System.out.println(syntax.getAsJoinedListString("itemname") + ": " + items);
                    List<MarketEntry> marketEntries = MarketSQL.searchFor(items);
                    marketBuilder(event, marketEntries, response, 0, Math.min(10, marketEntries.size()));
                }
            } else if (mode.equals("buy")){
                MarketEntry entry = MarketSQL.getEntryById(syntax.getAsInt("marketid"));
                if (entry == null)
                    response.setDescription("This Market ID does not exist!");
                else {
                    if (entry.getPrice() > userSQL.getMoney())
                        response
                                .setDescription("You don't have enough money to buy that item!")
                                .setFooter("You need " + (entry.getPrice() - userSQL.getMoney()) + " more coins", author.getEffectiveAvatarUrl());
                    else {
                        MarketSQL.removeEntry(entry.getMarketId());
                        User owner = entry.getOwner();
                        UserSQL ownerUserSQL = UserSQL.fromUser(owner);
                        InventorySQL ownerInv = InventorySQL.fromUser(owner);
                        Item boughtItem = entry.getItem();
                        userSQL.addMoney(-entry.getPrice());
                        ownerUserSQL.addMoney(entry.getPrice());
                        ownerInv.changeOwnership(entry.getInventoryId(), author.getId());
                        ownerInv.setItemAvailability(entry.getInventoryId(), InventoryItem.Availability.AVAILABLE);
                        response
                                .setDescription(String.format("You bought 1x *%s* from %s#%s for **%s**", boughtItem.getName(), owner.getName(), owner.getDiscriminator(), entry.getPriceDisplay()))
                                .setFooter("You have " + userSQL.getMoney() + "$ left.", owner.getEffectiveAvatarUrl());
                        owner.openPrivateChannel().queue(pc -> pc.sendMessage(
                                ChatTools.INFO(owner).setDescription(String.format("You sold 1x *%s* to %s#%s for **%s**", boughtItem.getName(), author.getName(), author.getDiscriminator(), entry.getPriceDisplay()))
                            .setFooter("You now have " + ownerUserSQL.getMoney() + "$.", author.getEffectiveAvatarUrl()).build()).queue());
                    }
                }
            } else if (mode.equals("remove")) {
                MarketEntry entry = MarketSQL.getEntryById(syntax.getAsInt("marketid"));
                if (entry == null)
                    response.setDescription("No Market Entry with the ID ``" + syntax.getAsInt("marketid") + "`` exists.");
                else {
                    if (entry.getOwnerId().equals(author.getId()) || Arrays.asList(Settings.INSTANCE.developers).contains(author.getId())){
                        MarketSQL.removeEntry(entry.getMarketId());
                        InventorySQL.fromUser(author).setItemAvailability(entry.getInventoryId(), InventoryItem.Availability.AVAILABLE);
                        response.setDescription("You successfully deleted the Market Entry with the ID ``" + entry.getMarketId() + "``.");
                    } else {
                        response.setDescription("You can only delete your own entries!");
                    }
                }
            }
            channel.sendMessage(response.build()).queue();
        });
    }

    private EmbedBuilder marketBuilder(CommandEvent event, List<MarketEntry> entries, EmbedBuilder eb, int start, int end){
        eb
                .setAuthor(event.getAuthor().getName() + " visits the market", Static.PLACEHOLDER_URL, event.getAuthor().getEffectiveAvatarUrl())
                .setDescription("To get more information about an item, type ``" + event.getUsedPrefix() + "market info <marketId>``\n\n" +
                        "**Your Search results:**\n");

        List<MarketEntry> qualified = entries.subList(start, end);
        for (MarketEntry entry : qualified){
            Item i = entry.getItem();
            User owner = entry.getOwner();
            eb.appendDescription(String.format("\n%s **Market ID: ``%s``** *%s*; Price: `%s` by %s#%s", i.getEmoteMention(), entry.getMarketId(), i.getName(), entry.getPriceDisplay(), owner.getName(), owner.getDiscriminator()));
        }
        if (entries.size() == 0)
            eb.appendDescription("\n*No results found*");
        return eb;
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("market")
                .setHelp("Visit the user-market, where you can sell and buy items from other users")
                .setSyntaxBuilder(new SyntaxBuilder(
                        new SyntaxBuilder()
                            .addSubcommand("mode", "sell")
                            .addElement("itemid", SyntaxElementType.INT)
                            .addElement("price", SyntaxElementType.INT),
                        new SyntaxBuilder()
                            .addSubcommand("mode", "sell")
                            .addElement("item", SyntaxElementType.STRING, true)
                            .addElement("price", SyntaxElementType.INT),

                        new SyntaxBuilder()
                            .addSubcommand("mode", "remove")
                            .addElement("marketid", SyntaxElementType.INT),

                        new SyntaxBuilder()
                            .addSubcommand("mode", "buy")
                            .addElement("marketid", SyntaxElementType.INT),


                        new SyntaxBuilder()
                            .addSubcommand("mode", "search")
                            .addElement("itemid", SyntaxElementType.INT),

                        new SyntaxBuilder()
                            .addSubcommand("mode", "search")
                            .addElement("itemname", SyntaxElementType.STRING, true)


                ));
    }
}
