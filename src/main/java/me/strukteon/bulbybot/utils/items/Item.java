package me.strukteon.bulbybot.utils.items;
/*
    Created by nils on 25.08.2018 at 15:36.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.utils.ChatTools;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Item {
    BG_ARCHITECTURE(0, "Architecture Background", Type.BACKGROUND, "/images/backgrounds/bg-architecture.jpg", "<:bg_architecture:483358589545742346>", false, false),
    BG_BEACH(1, "Beach Background", Type.BACKGROUND, "/images/backgrounds/bg-beach.jpg", "<:bg_beach:483358649578815518>", true, true, 600),
    BG_CODE(2, "Code Background", Type.BACKGROUND, "/images/backgrounds/bg-code.jpg", "<:bg_code:483358668927270928>", true, true, 600),
    BG_STARS(3, "Stars Background", Type.BACKGROUND, "/images/backgrounds/bg-stars.jpg", "<:bg_stars:483358656604143616>", true, true, 600),
    BG_HINTERSEE(4, "Hintersee Background", Type.BACKGROUND, "/images/backgrounds/bg-hintersee.jpg", "<:bg_hintersee:483736390752796683>", true, true, 600),
    BG_RAIN(5, "Rain Background", Type.BACKGROUND, "/images/backgrounds/bg-rain.jpg", "<:bg_rain:483736390044221481>", true, true, 600),
    BG_SPEED(6, "Speed Background", Type.BACKGROUND, "/images/backgrounds/bg-speed.jpg", "<:bg_speed:483736390941671440>", true, true, 600),
    BG_UNIVERSE(7, "Universe Background", Type.BACKGROUND, "/images/backgrounds/bg-universe.jpg", "<:bg_universe:483736391508033536>", true, true, 600),


    EMBED_COLOR_RANDOM(101, "Random Embed Color", Type.EMBED_COLOR, ChatTools::randomColor, "<:embed_random:483364919534944287>", true, true),
    EMBED_COLOR_GREEN(102, "Green Embed Color", Type.EMBED_COLOR, Color.GREEN, "<:embed_green:483364915923648513>", false, false),
    EMBED_COLOR_YELLOW(103, "Yellow Embed Color", Type.EMBED_COLOR, Color.YELLOW, "<:embed_white:483364920738447361>", true, true, 200),
    EMBED_COLOR_ORANGE(104, "Orange Embed Color", Type.EMBED_COLOR, Color.decode("#FF6600"), "<:embed_orange:483364917647245312>", true, true, 200),
    EMBED_COLOR_BLUE(105, "Blue Embed Color", Type.EMBED_COLOR, Color.decode("#7289DA"), "<:embed_blue:483364914698780673>", true, true, 200),
    EMBED_COLOR_WHITE(106, "White Embed Color", Type.EMBED_COLOR, Color.WHITE, "<:embed_yellow:483364922290339841>", true, true, 200),
    EMBED_COLOR_BLACK(107, "Black Embed Color", Type.EMBED_COLOR, Color.BLACK, "<:embed_black:483364913180311552>", true, true, 200),

    REDEEMABLE_MONEYBAG(201, "Bag of money", Type.REDEEMABLE, Redeemable.redeemableMoney(500), ":moneybag:", false, true),


    ;



    int id;
    String name;
    Type itemType;
    long price;

    Object additionalInfo;
    String emoteMention;

    boolean sellable;
    boolean tradeable;

    Item(int id, String name, Type itemType, AdditionalInfoInterface additionalInfo, String emoteMention, boolean sellable, boolean tradeable){
        this(id, name, itemType, additionalInfo, emoteMention, sellable, tradeable, -1);
    }

    Item(int id, String name, Type itemType, AdditionalInfoInterface additionalInfo, String emoteMention, boolean sellable, boolean tradeable, long price){
        this(id, name, itemType, (Object) additionalInfo, emoteMention, sellable, tradeable, price);
    }

    Item(int id, String name, Type itemType, Object additionalInfo, String emoteMention, boolean sellable, boolean tradeable){
        this(id, name, itemType, additionalInfo, emoteMention, sellable, tradeable, -1);
    }

    Item(int id, String name, Type itemType, Object additionalInfo, String emoteMention, boolean sellable, boolean tradeable, long price){
        this.id = id;
        this.name = name;
        this.itemType = itemType;
        this.additionalInfo = additionalInfo;
        this.emoteMention = emoteMention;
        this.sellable = sellable;
        this.tradeable = tradeable;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getItemType() {
        return itemType;
    }

    public Object getAdditionalInfo() {
        if (additionalInfo instanceof AdditionalInfoInterface)
            return ((AdditionalInfoInterface) additionalInfo).execute();
        return additionalInfo;
    }

    public String getEmoteMention() {
        return emoteMention;
    }

    public long getPrice() {
        return price;
    }

    public boolean isSellable() {
        return sellable;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public enum Type {
        BACKGROUND("Profile Background"),
        CHAT_ENHANCEMENT("Chat Enhancement"),
        EMBED_COLOR("Embed Color"),
        REDEEMABLE("Redeemable"),
        COLLECTIBLE("Collectible"),
        OTHER("Other");

        private String name;

        Type(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public static Item getItemById(int id){
        Item[] items = Item.values();
        for (Item i : items)
            if (i.getId() == id)
                return i;
        return null;
    }

    public static List<Item> getItemsByName(String name){
        return Arrays.stream(Item.values()).filter(item -> item.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
    }


    private interface AdditionalInfoInterface {
        Object execute();
    }
}
