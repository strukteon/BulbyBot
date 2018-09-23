package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 02.09.2018 at 15:35.
    
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
import me.strukteon.bulbybot.utils.items.Redeemable;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Redeem implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        EmbedBuilder response = ChatTools.INFO(author);
        Register.isRegistered(event, author, userSQL -> {
            InventorySQL inventorySQL = InventorySQL.fromUser(author);
            List<InventoryItem> available = inventorySQL.getAvailableItemsWithType(Item.Type.REDEEMABLE);
            if (available.size() == 0)
                response.setDescription("You don't have available redeemable items!");
            else {
                InventoryItem item = null;
                if (syntax.getExecutedBuilder() == 0) {
                    List<InventoryItem> withId = available.stream().filter(ii -> ii.getItemId() == syntax.getAsInt("itemid")).collect(Collectors.toList());
                    if (withId.size() == 0)
                        response.setDescription("You don't have redeemable items with that id!");
                    else
                        item = withId.get(0);
                } else {
                    Map<Integer, InventoryItem> uniqueAvailableItems = new HashMap<>();
                    available.stream().filter(ii -> ii.getItem().getName().toLowerCase().contains(syntax.getAsJoinedListString("itemname"))).forEach(ii -> {
                        if (!uniqueAvailableItems.containsKey(ii.getItemId()))
                            uniqueAvailableItems.put(ii.getItemId(), ii);
                    });
                    Collection<InventoryItem> items = uniqueAvailableItems.values();
                    if (items.size() == 0) {
                        response.setDescription("Sorry, but no item with the name ``" + syntax.getAsJoinedListString("itemname") + "`` is available in your inventory.");
                    } else if (items.size() > 1) {
                        response.setDescription("I found more than one item containing this text. Please try it again with a more detailed name.");
                        for (InventoryItem i : items)
                            response.appendDescription("\n*" + i.toString() + "*");
                    } else
                        item = uniqueAvailableItems.values().toArray(new InventoryItem[0])[0];
                }

                if (item != null){
                    Redeemable redeemable = ((Redeemable)item.getItem().getAdditionalInfo());
                    String redeemed;
                    System.out.println(redeemable);
                    System.out.println(redeemable.getRedeemType());
                    switch (redeemable.getRedeemType()){
                        case MONEY:
                            redeemed = redeemable.getAmount() + " coins";
                            userSQL.addMoney(redeemable.getAmount());
                            break;
                        case XP:
                            redeemed = redeemable.getAmount() + " xp";
                            userSQL.addXp(redeemable.getAmount());
                            break;
                        case ITEM:
                            redeemed = redeemable.getAmount() + "x " + redeemable.getItem().getName();
                            for (int i = 0; i < redeemable.getAmount(); i++)
                                inventorySQL.addItem(redeemable.getItem());
                            break;
                        default:
                            redeemed = "an error";
                    }
                    inventorySQL.removeItem(item.getInventoryId());
                    response.setDescription("You redeemed " + redeemed + ".");
                }
            }
        });
        channel.sendMessage(response.build()).queue();
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("redeem")
                .setSyntaxBuilder(new SyntaxBuilder(
                        new SyntaxBuilder()
                                .addElement("itemid", SyntaxElementType.INT),
                        new SyntaxBuilder()
                                .addElement("itemname", SyntaxElementType.STRING, true)
                ));
    }
}
