package me.strukteon.bettercommand.command;
/*
    Created by nils on 01.08.2018 at 21:03.
    
    (c) nils 2018
*/

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionManager {

    private List<Permission> requiredBotPerms = new ArrayList<>();
    private List<Permission> requiredUserPerms = new ArrayList<>();

    private List<String> limitedUsers = new ArrayList<>();

    public PermissionManager() { }

    public PermissionManager limitToUsers(String... userIds){
        limitedUsers = Arrays.asList(userIds);
        return this;
    }

    public PermissionManager limitToUsers(User... users){
        limitedUsers = Arrays.stream(users).map(User::getId).collect(Collectors.toList());
        return this;
    }

    public PermissionManager addRequiredBotPerms(Permission... requiredBotPerms) {
        return addRequiredBotPerms(Arrays.asList(requiredBotPerms));
    }

    public PermissionManager addRequiredBotPerms(Collection<Permission> requiredBotPerms) {
        this.requiredBotPerms.addAll(requiredBotPerms);
        return this;
    }

    public PermissionManager addRequiredUserPerms(Permission... requiredUserPerms) {
        return addRequiredUserPerms(Arrays.asList(requiredUserPerms));
    }

    public PermissionManager addRequiredUserPerms(Collection<Permission> requiredUserPerms) {
        this.requiredUserPerms.addAll(requiredUserPerms);
        return this;
    }

    public List<String> getLimitedUsers() {
        return limitedUsers;
    }

    public List<Permission> getRequiredBotPerms() {
        return requiredBotPerms == null ? new ArrayList<>() : requiredBotPerms;
    }

    public List<Permission> getRequiredUserPerms() {
        return requiredUserPerms == null ? new ArrayList<>() : requiredUserPerms;
    }
}
