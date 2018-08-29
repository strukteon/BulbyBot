package me.strukteon.bettercommand;
/*
    Created by nils on 29.07.2018 at 21:01.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.command.BaseCommand;

import java.util.Arrays;
import java.util.Collection;

public class CommandSection {
    private String name;
    private Collection<BaseCommand> commands;

    public CommandSection(String name, Collection<BaseCommand> commands){
        this.name = name;
        this.commands = commands;
    }
    public CommandSection(String name, BaseCommand... commands){
        this(name, Arrays.asList(commands));
    }

    public String getName() {
        return name;
    }

    public Collection<BaseCommand> getCommands() {
        return commands;
    }
}
