package me.strukteon.bulbybot.core.sql;
/*
    Created by nils on 29.04.2018 at 19:41.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.core.CLI;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MySQL {

    private Connection connection;
    private String
            host,
            user,
            password,
            database;
    private int port;


    /**
     * @param host      Host of server
     * @param port      Port of server
     * @param user      User of database
     * @param password  Password of user
     * @param database  Database of server
     */
    public MySQL(String host, int port, String user, String password, String database){
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }


    /**
     * Connect to the database
     * @return MySQL object
     */
    public MySQL connect(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?allowMultiQueries=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", this.user, this.password);
        } catch (SQLException e) {
            CLI.error("Could not establish MySQL connection to " + this.host);
            CLI.error(e);
            CLI.shutdown(1);
        }
        return this;
    }



    /**
     * Close the connection
     * @return MySQL object
     */
    public MySQL disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            CLI.error("Could not disconnect from " + this.host);
            CLI.error(e);
        }
        return this;
    }


    public Connection getConnection(){
        return connection;
    }


    /**
     * Select columns from table
     * @param selection Columns to select
     * @param table table
     * @param where where condition
     * @return HashMap<String, String> of the result
     */
    public HashMap<String, String> SELECT(String selection, String table, String where){
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            PreparedStatement ps = connection.prepareStatement(String.format("select %s from `%s` where %s", selection, table, where));

            ResultSet res = ps.executeQuery();
            ResultSetMetaData meta = res.getMetaData();

            if (res.first())
                for (int i = 1; i <= meta.getColumnCount(); i++)
                    hashMap.put(meta.getColumnName(i), res.getString(i));
            else
                return hashMap;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashMap;
    }


    /**
     * Drop/Add columns to table until it matches with given columns
     * @param table tablename
     * @param columns final columns the table should have
     * @return boolean anything changed
     */
    public boolean MATCH_COLUMNS(String table, String[] columns){
        List<String> sourceColumns = new ArrayList<>();
        List<String> endColumns = Arrays.asList(columns);
        StringBuilder finalSql = new StringBuilder();
        try {
            PreparedStatement ps = connection.prepareStatement(String.format("select * from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME='%s' AND NOT COLUMN_NAME='USER' AND NOT COLUMN_NAME='CURRENT_CONNECTIONS' AND NOT COLUMN_NAME='TOTAL_CONNECTIONS'", table));

            ResultSet res = ps.executeQuery();

            while (res.next())
                sourceColumns.add(res.getString("COLUMN_NAME"));

            for (String s : sourceColumns)
                if (!endColumns.contains(s))
                    finalSql.append(String.format("alter table `%s` drop column `%s`; ", table, s));

            for (String s : endColumns)
                if (!sourceColumns.contains(s))
                    finalSql.append(String.format("alter table `%s` add column `%s` text; ", table, s));

            if (finalSql.length() != 0)
                connection.createStatement().execute(finalSql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return finalSql.length() != 0;
    }

    /**
     * Drop/Add columns to table until it matches with given columns
     * @param table tablename
     * @param columns final columns the table should have
     * @param types column types
     * @return boolean anything changed
     */
    public boolean MATCH_COLUMNS(String table, String[] columns, String[] types){
        List<String> sourceColumns = new ArrayList<>();
        List<String> endColumns = Arrays.asList(columns);
        StringBuilder finalSql = new StringBuilder();
        try {
            PreparedStatement ps = connection.prepareStatement(String.format("select * from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME='%s' AND NOT COLUMN_NAME='USER' AND NOT COLUMN_NAME='CURRENT_CONNECTIONS' AND NOT COLUMN_NAME='TOTAL_CONNECTIONS'", table));

            ResultSet res = ps.executeQuery();

            while (res.next())
                sourceColumns.add(res.getString("COLUMN_NAME"));

            for (String s : sourceColumns)
                if (!endColumns.contains(s))
                    finalSql.append(String.format("alter table `%s` drop column `%s`; ", table, s));

            for (String s : endColumns)
                if (!sourceColumns.contains(s))
                    finalSql.append(String.format("alter table `%s` add column `%s` " + types[endColumns.indexOf(s)] + "; ", table, s));

            if (finalSql.length() != 0)
                connection.createStatement().execute(finalSql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return finalSql.length() != 0;
    }


    /**
     * Insert values into the table
     * @param table table
     * @param columns columns
     * @param values values
     */
    public void INSERT(String table, String columns, String values){
        try {
            connection.createStatement().execute(String.format("insert into `%s` (%s) values (%s)", table, columns, values));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Update values in a table
     * @param table tablename
     * @param values values
     * @param where where condition
     */
    public void UPDATE(String table, String values, String where){
        try {
            connection.createStatement().execute(String.format("update `%s` set %s where %s", table, values, where));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Delete values from a table
     * @param table tablename
     * @param where where condition
     */
    public void DELETE(String table, String where){
        try {
            connection.createStatement().execute(String.format("delete from `%s` where %s", table, where));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Create a new Table
     * @param table tablename
     * @param columns columns
     */
    public void CREATE_TABLE(String table, String[] columns){
        try {
            StringBuilder b = new StringBuilder();
            for (String s : columns)
                b.append(s + " text, ");
            b.delete(b.length()-2, b.length());
            connection.createStatement().execute(String.format("create table `%s` (%s)", table, b.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Drop a table
     * @param table tablename
     */
    public void DROP_TABLE(String table){
        try {
            connection.createStatement().execute(String.format("drop table `%s`", table));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Add a column to a table
     * @param table tablename
     * @param column column to add
     */
    public void ALTER_TABLE_ADD(String table, String column){
        try {
            connection.createStatement().execute(String.format("alter table `%s` add %s text", table, column));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Remove a column from a table
     * @param table tablename
     * @param column column to remove
     */
    public void ALTER_TABLE_DROP(String table, String column){
        try {
            connection.createStatement().execute(String.format("alter table `%s` drop column `%s`", table, column));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Check if a table exists
     * @param table tablename
     * @return boolean
     */
    public boolean TABLE_EXISTS(String table){
        try {
            connection.createStatement().execute(String.format("select * from `%s` where 0", table));
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    /**
     * Check if a column exists
     * @param table tablename
     * @param column column
     * @return boolean
     */
    public boolean COLUMN_EXISTS(String table, String column){
        try {
            connection.createStatement().execute(String.format("select `%s` from `%s` where 0", column, table));
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

}
