package me.strukteon.bulbybot.core.sql.market;
/*
    Created by nils on 26.08.2018 at 23:41.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.BulbyBot;
import me.strukteon.bulbybot.core.sql.inventory.InventoryItem;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.entities.User;

public class MarketEntry {
    int marketId;
    String ownerId;
    int inventoryId;
    int itemId;
    int price;

    protected MarketEntry(String marketId, String ownerId, String inventoryId, String itemId, String price){
        this(Integer.parseInt(marketId), ownerId, Integer.parseInt(inventoryId), Integer.parseInt(itemId), Integer.parseInt(price));
    }

    protected MarketEntry(int marketId, String ownerId, int inventoryId, int itemId, int price){
        this.marketId = marketId;
        this.ownerId = ownerId;
        this.inventoryId = inventoryId;
        this.itemId = itemId;
        this.price = price;
    }

    public int getMarketId() {
        return marketId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public User getOwner(){
        return BulbyBot.getShardManager().getUserById(ownerId);
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public InventoryItem getInventoryItem(){
        return InventorySQL.getInventoryItem(inventoryId);
    }

    public int getItemId() {
        return itemId;
    }

    public Item getItem(){
        return Item.getItemById(itemId);
    }

    public int getPrice() {
        return price;
    }

    public String getPriceDisplay(){
        return price == 0 ? "Free" : price == -1 ? "Infinity" : price + "$";
    }
}
