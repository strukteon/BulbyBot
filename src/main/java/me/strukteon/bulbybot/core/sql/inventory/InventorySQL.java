package me.strukteon.bulbybot.core.sql.inventory;
/*
    Created by nils on 25.08.2018 at 19:18.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.core.sql.MySQL;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventorySQL {
    private static MySQL mySQL;
    private static String table = "inventory";

    private String userid;

    public static void init(MySQL mySQL){
        InventorySQL.mySQL = mySQL;
    }


    private InventorySQL(String userid){
        this.userid = userid;
    }

    public static InventorySQL fromUserId(String userid){
        return new InventorySQL(userid);
    }

    public static InventorySQL fromUser(User user){
        return fromUserId(user.getId());
    }

    public void addItem(Item item){
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
    }

    public static InventoryItem getInventoryItem(int inventoryId){
        Map<String, String> itemSet = mySQL.SELECT("*", table, "id=" + inventoryId);
        return new InventoryItem(itemSet.get("id"), itemSet.get("ownerid"), itemSet.get("itemid"), itemSet.get("available"));
    }
}
