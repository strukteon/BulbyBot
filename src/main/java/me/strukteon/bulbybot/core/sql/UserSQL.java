package me.strukteon.bulbybot.core.sql;
/*
    Created by nils on 30.04.2018 at 12:09.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.utils.Settings;
import me.strukteon.bulbybot.utils.Static;
import net.dv8tion.jda.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

public class UserSQL {
    private static MySQL mySQL;
    private static String table = "users";
    private static String[] columns = {"id", "bio", "money", "lastdaily", "badges", "command_count", "xp"};
    private static String[] columnTypes = {"bigint", "text", "int", "text", "text", "int", "int"};

    private static Map<String, Boolean> userCache = new HashMap<>(); // using this to lower db requests

    private String userid;

    public static void init(MySQL mySQL){
        UserSQL.mySQL = mySQL;

        if (!mySQL.TABLE_EXISTS(table))
            mySQL.CREATE_TABLE(table, columns);

        //mySQL.MATCH_COLUMNS(table, columns, columnTypes);
    }


    private UserSQL(String userid){
        this.userid = userid;
    }

    public static UserSQL fromUserId(String userid){
        return new UserSQL(userid);
    }

    public static boolean existsFromUser(User user){
        return new UserSQL(user.getId()).exists();
    }

    public static UserSQL fromUser(User user){
        UserSQL userSQL = new UserSQL(user.getId());
        if (userSQL.exists())
            return userSQL;
        return null;
    }

    public static UserSQL newUser(User user){
        UserSQL userSQL = new UserSQL(user.getId());
        if (userSQL.exists())
            userSQL.create();
        return userSQL;
    }

    @Deprecated
    public static UserSQL fromUserIfExists(User user){
        UserSQL userSQL = new UserSQL(user.getId());
        if (userSQL.exists())
            return userSQL;
        return null;
    }


    public boolean exists(){
        if (userCache.containsKey(userid))
            return userCache.get(userid);
        boolean exists = mySQL.SELECT("*", table, "id='"+userid+"'").size() != 0;
        userCache.put(userid, exists);
        return exists;
    }

    public void create(){
        mySQL.INSERT(table, "`id`, `bio`, `money`, `lastdaily`, `badges`, `command_count`", String.format("'%s', 'no bio set', 0, '%s', '', 0", userid, Static.DATE_FORMAT.format(new Date(0))));
        userCache.put(userid, true);
    }


    public String getBio(){
        return mySQL.SELECT("*", table, "id='"+userid+"'").get("bio");
    }

    public Date getLastDaily(){
        try {
            return Static.DATE_FORMAT.parse(mySQL.SELECT("*", table, "id='"+userid+"'").get("lastdaily"));
        } catch (ParseException e) {
            return new Date(0L);
        }
    }

    public long getMoney(){
        try {
            String s = mySQL.SELECT("*", table, "id='" + userid + "'").get("money");
            return Long.parseLong(s);
        } catch (NumberFormatException e){
            return 0;
        }
    }

    public long getXp(){
        try {
            String s = mySQL.SELECT("*", table, "id='" + userid + "'").get("xp");
            System.out.println("xp " + s);
            return Long.parseLong(s);
        } catch (NumberFormatException e){
            return 0;
        }
    }

    public List<Badge> getBadges(){
        List<Badge> badges = new ArrayList<>();
        String content = mySQL.SELECT("*", table, "id='"+userid+"'").get("badges");
        if (!"".equals(content))
            Arrays.asList(content.split(" "))
                    .forEach(s -> badges.add(Badge.valueOf(s)));
        return badges;
    }

    public long getCommandCount(){
        return Long.parseLong(mySQL.SELECT("*", table, "id='"+userid+"'").get("command_count"));
    }


    public void increaseCommandCount(){
        mySQL.UPDATE(table, "command_count=command_count+1", "id='"+userid+"'");
    }

    public void setBio(String bio){
        mySQL.UPDATE(table, "bio='"+bio.replace("'", "\\'")+"'", "id='"+userid+"'");
    }

    public void setMoney(long money){
        mySQL.UPDATE(table, "money="+money, "id='"+userid+"'");
    }

    public void addMoney(long money){
        mySQL.UPDATE(table, "money=money+"+money, "id='"+userid+"'");
    }

    public void setXp(int xp){
        mySQL.UPDATE(table, "xp="+xp, "id='"+userid+"'");
    }

    public void addXp(int xp){
        mySQL.UPDATE(table, "`xp`=`xp`+"+xp, "id='"+userid+"'");
    }

    public int getRank(){
        try {
            return Integer.parseInt(mySQL.SELECT("id, xp, FIND_IN_SET( xp, (    \n" +
                    "SELECT GROUP_CONCAT( xp ORDER BY xp DESC ) \n" +
                    "FROM "+table+" )\n" +
                    ") AS rank", table, "id='"+userid+"'").get("rank"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Deprecated
    public int getRankSize(){
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("show table status from " + Settings.INSTANCE.mySqlSettings.database + " where Name='users'");

            ResultSet res = ps.executeQuery();
            ResultSetMetaData meta = res.getMetaData();

            if (res.first())
                for (int i = 1; i <= meta.getColumnCount(); i++)
                    if (meta.getColumnName(i).equalsIgnoreCase("table_rows"))
                        return Integer.parseInt(res.getString(i));

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalUserSize(){
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("show table status from " + Settings.INSTANCE.mySqlSettings.database + " where Name='users'");

            ResultSet res = ps.executeQuery();
            ResultSetMetaData meta = res.getMetaData();

            if (res.first())
                for (int i = 1; i <= meta.getColumnCount(); i++)
                    if (meta.getColumnName(i).equalsIgnoreCase("table_rows"))
                        return Integer.parseInt(res.getString(i));

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setLastDaily(Date date){
        mySQL.UPDATE(table, "lastdaily='"+Static.DATE_FORMAT.format(date)+"'", "id='"+userid+"'");
    }

    public void setBadges(List<Badge> badges){
        StringBuilder b = new StringBuilder();
        badges.forEach(badge -> b.append(badge.name()).append(" "));
        b.deleteCharAt(b.length()-1);
        mySQL.UPDATE(table, "badges='"+b.toString()+"'", "id='"+userid+"'");
    }

    public enum Badge {
        DEVELOPER("Developer", "One of the core developers of the bot", "badge-dev.png"),
        STAFF("Staff", "Staff member of the bot", "badge-staff.png"),
        BETA_USER("Beta user", "Used this bot before june 2018", "badge-beta.png"),
        ACCEPTED("Accepted user", "Special badge rewarded by strukteon himself", "badge-accepted.png");

        private String title;
        private String description;
        private String fileName;

        Badge(String title, String description, String fileName){
            this.title = title;
            this.description = description;
            this.fileName = fileName;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getResourcePath(){
            return Static.Files.PROFILE_BADGES_DIR + fileName;
        }
    }

}
