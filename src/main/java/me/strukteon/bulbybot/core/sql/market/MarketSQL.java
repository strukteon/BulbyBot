package me.strukteon.bulbybot.core.sql.market;
/*
    Created by nils on 26.08.2018 at 23:39.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.core.sql.MySQL;
import me.strukteon.bulbybot.core.sql.inventory.InventoryItem;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MarketSQL {
    private static MySQL mySQL;
    private static String table = "market";

    private String userid;

    public static void init(MySQL mySQL){
        MarketSQL.mySQL = mySQL;
    }


    private MarketSQL(String userid){
        this.userid = userid;
    }

    public static MarketSQL fromUserId(String userid){
        return new MarketSQL(userid);
    }

    public static MarketSQL fromUser(User user){
        return fromUserId(user.getId());
    }

    public static MarketEntry addEntry(int inventoryId, int price){
        InventoryItem item = InventorySQL.getInventoryItem(inventoryId);
        mySQL.INSERT(table, "`ownerid`, `inventoryid`, `itemid`, `price`", String.format("'%s', %s, %s, %s", item.getOwnerId(), inventoryId, item.getItemId(), price));
        int id = Integer.parseInt(mySQL.SELECT("LAST_INSERT_ID()", table, "1").get("LAST_INSERT_ID()"));
        return new MarketEntry(id, item.getOwnerId(), inventoryId, item.getItemId(), price);
    }

    public static MarketEntry addEntry(InventoryItem item, int price){
        mySQL.INSERT(table, "`ownerid`, `inventoryid`, `itemid`, `price`", String.format("'%s', %s, %s, %s", item.getOwnerId(), item.getInventoryId(), item.getItemId(), price));
        int id = Integer.parseInt(mySQL.SELECT("LAST_INSERT_ID()", table, "1").get("LAST_INSERT_ID()"));
        return new MarketEntry(id, item.getOwnerId(), item.getInventoryId(), item.getItemId(), price);
    }

    public static MarketEntry getEntryById(int marketId){
        HashMap<String, String> entrySet = mySQL.SELECT("*", table, "marketid=" + marketId);
        System.out.println(entrySet.size());
        if (entrySet.size() == 0)
            return null;
        return new MarketEntry(entrySet.get("marketid"), entrySet.get("ownerid"), entrySet.get("inventoryid"), entrySet.get("itemid"), entrySet.get("price"));
    }

    public static void removeEntry(int marketid){
        mySQL.DELETE(table, "marketid=" + marketid);
    }

    public static List<MarketEntry> searchFor(Item item){
        return searchFor(List.of(item), SortType.ASCENDING);
    }

    public static List<MarketEntry> searchFor(int itemid){
        return searchFor(List.of(itemid), SortType.ASCENDING);
    }

    public static List<MarketEntry> searchFor(Collection<Item> items){
        return searchFor(items, SortType.ASCENDING);
    }
    public static List<MarketEntry> searchFor(Collection<Item> items, SortType sortType){
        return searchFor(items.stream().map(Item::getId).collect(Collectors.toList()), sortType);
    }

    public static List<MarketEntry> searchFor(List<Integer> itemIds, SortType sortType){
        List<MarketEntry> entries = new ArrayList<>();
        StringBuilder items = new StringBuilder();
        itemIds.forEach(i -> {
            if (items.length() != 0)
                items.append(" or ");
            items.append("itemid=" + i);
        });
        try {
            ResultSet res = mySQL.getConnection().prepareStatement(String.format("select * from %s where %s order by price %s", table, items.toString(), sortType.asMySql)).executeQuery();
            while (res.next())
                entries.add(new MarketEntry(res.getInt("marketid"), res.getString("ownerid"), res.getInt("inventoryid"),
                        res.getInt("itemid"), res.getInt("price")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public enum SortType {
        ASCENDING("ASC"),
        DESCENDING("DESC");
        private String asMySql;
        SortType(String asMySql){
            this.asMySql = asMySql;
        }
    }

/*
    public void a(Item item){
        addItem(item, InventoryItem.Availability.AVAILABLE);
    }

    public void addItem(Item item, InventoryItem.Availability available){
        mySQL.INSERT(table, "`ownerid`, `itemid`, `itemtype`, `available`", String.format("'%s', '%s', '%s', '%s'", userid, item.getId(), item.getItemType(), available.getId()));
    }

    public void setItemAvailability(int inventoryId, InventoryItem.Availability availability){
        mySQL.UPDATE(table, "available=" + availability.getId(), "id=" + inventoryId);
    }

    public void setUsedItem(int inventoryId, Item.Type itemType){
        mySQL.UPDATE(table, "available=0", "ownerid='" + userid + "' and available=2 and itemtype='" + itemType.name() + "'");
        mySQL.UPDATE(table, "available=2", "id=" + inventoryId + " and available=0");
    }

    public void removeItem(int inventoryId){
        mySQL.DELETE(table, "id=" + inventoryId);
    }

    public void changeOwnership(int inventoryId, String newOwnerId){
        mySQL.UPDATE(table, "ownerid='" + newOwnerId + "'", "id=" + inventoryId);
    }

    public InventoryItem getUsedItem(Item.Type itemType){
        Map<String, String> itemSet = mySQL.SELECT("*", table, "ownerid='" + userid + "' and available=2 and itemtype='" + itemType.name() + "'");
        InventoryItem item = new InventoryItem(itemSet.get("id"), itemSet.get("ownerid"), itemSet.get("itemid"), itemSet.get("available"));
        return item;
    }

    public boolean hasAvailableItem(Item item){
        return ! mySQL.SELECT("*", table, "ownerid='" + userid + "' and available=0 and itemid=" + item.getId()).isEmpty();
    }

    public InventoryItem getAvailableItem(Item item){
        Map<String, String> itemSet = mySQL.SELECT("*", table, "ownerid='" + userid + "' and available=0 and itemid=" + item.getId());
        InventoryItem available = new InventoryItem(itemSet.get("id"), itemSet.get("ownerid"), itemSet.get("itemid"), itemSet.get("available"));
        return available;
    }

    public List<InventoryItem> getItems(){
        List<InventoryItem> items = new ArrayList<>();
        try {
            ResultSet res = mySQL.getConnection().prepareStatement(String.format("select * from %s where ownerid='%s'", table, userid)).executeQuery();
            while (res.next())
                items.add(new InventoryItem(res.getInt("id"), res.getString("ownerid"), res.getInt("itemid"),
                        InventoryItem.Availability.values()[res.getInt("available")]));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<InventoryItem> getAvailableItems(){
        List<InventoryItem> items = new ArrayList<>();
        try {
            ResultSet res = mySQL.getConnection().prepareStatement(String.format("select * from %s where ownerid='%s' and available=0", table, userid)).executeQuery();
            while (res.next())
                items.add(new InventoryItem(res.getInt("id"), res.getString("ownerid"), res.getInt("itemid"),
                        InventoryItem.Availability.values()[res.getInt("available")]));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<InventoryItem> getItemsWithType(Item.Type itemType){
        List<InventoryItem> items = new ArrayList<>();
        try {
            ResultSet res = mySQL.getConnection().prepareStatement(String.format("select * from %s where ownerid='%s' and itemtype='%s'", table, userid, itemType.name())).executeQuery();
            while (res.next())
                items.add(new InventoryItem(res.getInt("id"), res.getString("ownerid"), res.getInt("itemid"),
                        InventoryItem.Availability.values()[res.getInt("available")]));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }*/
}

