package me.strukteon.bulbybot.core.sql;
/*
    Created by nils on 30.04.2018 at 19:37.
    
    (c) nils 2018
*/

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatreonSQL {
    private static MySQL mySQL;
    private static String table = "patrons";
    private static String[] columns = {"patron_id", "discord_id", "patron_status", "last_status", "pledge_amount", "lifetime_support", "pledge_started", "last_charged"};

    private static SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String userid;

    public static void init(MySQL mySQL){
        PatreonSQL.mySQL = mySQL;

        if (!mySQL.TABLE_EXISTS(table))
            mySQL.CREATE_TABLE(table, columns);

        //mySQL.MATCH_COLUMNS(table, columns);
    }


    private PatreonSQL(String userid){
        this.userid = userid;
    }


    public static PatreonSQL fromUserId(String userid){
        return new PatreonSQL(userid);
    }

    public static PatreonSQL fromUser(User user){
        return fromUserId(user.getId());
    }

    public static PatreonSQL fromMember(Member member){
        return fromUser(member.getUser());
    }

    public static boolean isPatron(User user){
        PatreonSQL patreonSQL = PatreonSQL.fromUser(user);
        return patreonSQL.exists() && patreonSQL.isValidPatron();
    }


    public boolean exists(){
        return mySQL.SELECT("*", table, "`discord_id`='"+userid+"'").size() != 0;
    }

    public long getPatronId(){
        try {
            return Long.parseLong(mySQL.SELECT("*", table, "`discord_id`="+userid).get("patron_id"));
        } catch (NumberFormatException e){
            return 0;
        }
    }

    public String getPatronStatus(){
        return mySQL.SELECT("*", table, "`discord_id`="+userid).get("patron_status");
    }

    public boolean isValidPatron(){
        return "paid".equalsIgnoreCase(getLastStatus());
    }

    public String getLastStatus(){
        return mySQL.SELECT("*", table, "`discord_id`='"+userid+"'").get("last_status");
    }

    public int getPledgeAmount(){
        try {
            return Integer.parseInt(mySQL.SELECT("*", table, "`discord_id`="+userid).get("pledge_amount"));
        } catch (NumberFormatException e){
            return 0;
        }
    }

    public int getLifetimeSupport(){
        try {
            return Integer.parseInt(mySQL.SELECT("*", table, "`discord_id`="+userid).get("lifetime_support"));
        } catch (NumberFormatException e){
            return 0;
        }
    }

    public Date getPledgeStarted(){
        try {
            return TIMESTAMP_FORMAT.parse(mySQL.SELECT("*", table, "discord_id="+userid).get("pledge_started"));
        } catch (ParseException e) {
            return new Date(0);
        }
    }

    public Date getLastCharged(){
        try {
            return TIMESTAMP_FORMAT.parse(mySQL.SELECT("*", table, "discord_id="+userid).get("last_charged"));
        } catch (ParseException e) {
            return new Date(0);
        }
    }


}
