package me.strukteon.bulbybot.utils.items;
/*
    Created by nils on 25.08.2018 at 17:31.
    
    (c) nils 2018
*/


public class Redeemable {
    private RedeemType redeemType;

    private Redeemable(RedeemType type, Item item, int amount){
        this.redeemType = type;
        this.item = item;
        this.amount = amount;
    }

    private Item item;
    private int amount;


    public static Redeemable redeemableItem(Item item){
        return new Redeemable(RedeemType.ITEM, item, 1);
    }

    public static Redeemable redeemableItem(Item item, int amount){
        return new Redeemable(RedeemType.ITEM, item, amount);
    }

    public static Redeemable redeemableMoney(int amount){
        return new Redeemable(RedeemType.MONEY, null, amount);
    }

    public static Redeemable redeemableXp(int amount){
        return new Redeemable(RedeemType.XP, null, amount);
    }

    public RedeemType getRedeemType() {
        return redeemType;
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }


    public enum RedeemType {
        ITEM,
        MONEY,
        XP
    }
}
