package me.strukteon.bulbybot.utils;
/*
    Created by nils on 23.08.2018 at 20:18.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.core.sql.UserSQL;
import me.strukteon.bulbybot.core.sql.inventory.InventoryItem;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class ChatTools {

    public static EmbedBuilder ERROR(){
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Static.COLOR_RED);
        return eb;
    }

    @Deprecated
    public static EmbedBuilder INFO(){
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Static.COLOR_GREEN);
        return eb;
    }

    public static EmbedBuilder INFO(User user){
        EmbedBuilder eb = new EmbedBuilder();
        if (UserSQL.existsFromUser(user))
            eb.setColor((Color) InventorySQL.fromUser(user).getUsedItem(Item.Type.EMBED_COLOR).getItem().getAdditionalInfo());
        else
            eb.setColor(Color.GREEN);
        return eb;
    }

    public static Color randomColor(){
        return Color.decode("#" + Integer.toHexString((int)(Math.random()*256)) + Integer.toHexString((int)(Math.random()*256)) + Integer.toHexString((int)(Math.random()*256)));
    }

    public static <E> void getItemFromList(String name, EmbedBuilder response, Collection<E> availableItems, String locationWhereAvailable, Consumer<E> consumer){
        Collection<E> items = availableItems;
        if (items.size() == 0) {
            response.setDescription("Sorry, but no item with the name ``" + name + "`` is available in " + locationWhereAvailable + ".");
        } else if (items.size() > 1) {
            response.setDescription("I found more than one item containing this text. Please try it again with a more detailed name.");
            for (E i : items)
                response.appendDescription("\n*" + i.toString() + "*");
        } else
            consumer.accept((E) items.toArray()[0]);
    }

    public static void getUniqueItemFromList(String name, EmbedBuilder response, List<InventoryItem> availableItems, String locationWhereAvailable, Consumer<InventoryItem> consumer){
        Map<Integer, InventoryItem> uniqueAvailableItems = new HashMap<>();
        availableItems.forEach(ii -> {
            if (!uniqueAvailableItems.containsKey(ii.getItemId()))
                uniqueAvailableItems.put(ii.getItemId(), ii);
        });
        Collection<InventoryItem> items = uniqueAvailableItems.values();
        if (items.size() == 0) {
            response.setDescription("Sorry, but no item with the name ``" + name + "`` is available in " + locationWhereAvailable + ".");
        } else if (items.size() > 1) {
            response.setDescription("I found more than one item containing this text. Please try it again with a more detailed name.");
            for (InventoryItem i : items)
                response.appendDescription("\n*" + i.toString() + "*");
        } else
            consumer.accept(items.toArray(new InventoryItem[0])[0]);
    }

    public static class CheckList {
        public static int
                MODE_WAITING = 0,
                MODE_LOADING = 1,
                MODE_FINISHED = 2,
                MODE_CANCELED = 3;

        private List<String> items;
        private int[] completeStates;

        private String state = "Everything okay!";
        private Color color = Color.GREEN;

        private static String[] emotes = {":clock3:", Emotes.SPINNER.getAsEmote().getAsMention(), ":white_check_mark:", ":x:"};

        public CheckList(String... items){
            this(Arrays.asList(items));
        }

        public CheckList(List<String> items){
            this.items = items;
            this.completeStates = new int[items.size()];
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setMode(int itemPos, int mode){
            completeStates[itemPos] = mode;
        }

        public String getMessage(){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < items.size(); i++){
                if (sb.length() > 0)
                    sb.append("\n");
                sb.append(emotes[completeStates[i]] + " " + items.get(i));
            }
            return sb.toString();
        }

        public EmbedBuilder getEmbedBuilder(){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(color)
                    .addField("State:", state, false)
                    .addField("Queue:", getMessage(), false);
            return eb;
        }

    }
}
