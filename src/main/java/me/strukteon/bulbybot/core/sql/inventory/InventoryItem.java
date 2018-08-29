package me.strukteon.bulbybot.core.sql.inventory;
/*
    Created by nils on 26.08.2018 at 19:15.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.utils.items.Item;

public class InventoryItem {
    private int inventoryId;
    private String ownerId;
    private int itemId;
    private Availability available;

    protected InventoryItem(String inventoryId, String ownerId, String itemId, String available){
        this(Integer.parseInt(inventoryId), ownerId, Integer.parseInt(itemId), Availability.values()[Integer.parseInt(available)]);
    }

    protected InventoryItem(int inventoryId, String ownerId, int itemId, Availability available){
        this.inventoryId = inventoryId;
        this.ownerId = ownerId;
        this.itemId = itemId;
        this.available = available;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Item getItem() {
        return Item.getItemById(itemId);
    }

    public int getItemId() {
        return itemId;
    }

    public Availability getAvailability() {
        return available;
    }

    @Override
    public String toString() {
        return getItem().getName();
    }

    public enum Availability {
        AVAILABLE(0, "Available"),
        IN_MARKET(1, "In market"),
        IN_USE(2, "In use");

        private int id;
        private String name;

        Availability(int id, String name){
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        protected int getId() {
            return id;
        }
    }
}