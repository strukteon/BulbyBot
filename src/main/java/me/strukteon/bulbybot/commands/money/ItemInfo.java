package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 25.08.2018 at 17:51.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class ItemInfo implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        Item item;
        EmbedBuilder eb = ChatTools.INFO(author);
        if (syntax.getExecutedBuilder() == 0){
            item = Item.getItemById(syntax.getAsInt("itemid"));
            if (item == null){
                eb.setDescription("Sorry, but no item with the id ``" + syntax.getAsInt("itemid") + "`` exists.");
                channel.sendMessage(eb.build()).queue();
                return;
            }
        } else {
            List<Item> items = Item.getItemsByName(syntax.getAsJoinedListString("itemname"));
            if (items.size() == 0){
                eb.setDescription("Sorry, but no item with the name ``" + syntax.getAsJoinedListString("itemname") + "`` exists.");
                channel.sendMessage(eb.build()).queue();
                return;
            } else if (items.size() > 1){
                eb.setDescription("I found more than one item containing this text. Please try it again with a more detailed name.");
                for (Item i : items)
                    eb.appendDescription("\n*" + i.getName() + "*");
                channel.sendMessage(eb.build()).queue();
                return;
            }
            item = items.get(0);
        }
        setItemInfo(eb, item);
        channel.sendMessage(eb.build()).queue();
    }

    EmbedBuilder setItemInfo(EmbedBuilder eb, Item item){
        eb.setTitle("Infos about ``" + item.getName() + "`` " + item.getEmoteMention())
                .appendDescription("Sell this item on the market: " + trueOrFalse(item.isSellable()) + "\nTrade this item with other users: " + trueOrFalse(item.isTradeable()))
                .appendDescription("\n\nPrice in the shop: ``" + truePrice(item.getPrice()) + "``")
                .appendDescription("\n\nID: **" + item.getId() + "**")
                .appendDescription("\nItem Type: *" + item.getItemType().getName() + "*");

        return eb;
    }

    String trueOrFalse(boolean b){
        return b ? ":white_check_mark:" : ":x:";
    }

    String truePrice(long price){
        return price == -1 ? "Infinity" : price + "";
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("iteminfo")
                .setHelp("Get some informations about a sspecific item")
                .setAliases("ii")
                .setSyntaxBuilder(new SyntaxBuilder(
                        new SyntaxBuilder()
                            .addElement("itemid", SyntaxElementType.INT),
                        new SyntaxBuilder()
                            .addElement("itemname", SyntaxElementType.STRING, true)
                ));
    }
}
