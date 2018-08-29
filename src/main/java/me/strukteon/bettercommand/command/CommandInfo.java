package me.strukteon.bettercommand.command;
/*
    Created by nils on 31.07.2018 at 23:58.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.syntax.SyntaxBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandInfo {

    private String label;
    private String help = "no help set";

    private List<String> aliases = new ArrayList<>();

    private SyntaxBuilder syntaxBuilder;
    private String syntax;

    private boolean nsfwOnly = false;

    private long cooldown;


    public CommandInfo(String label){
        this.label = label;
    }

    public CommandInfo setCooldown(long cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public CommandInfo setHelp(String help) {
        this.help = help;
        return this;
    }

    public CommandInfo setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public CommandInfo setAliases(String... aliases) {
        this.aliases = Arrays.asList(aliases);
        return this;
    }

    public CommandInfo setSyntaxBuilder(SyntaxBuilder syntaxBuilder) {
        this.syntaxBuilder = syntaxBuilder;
        return this;
    }

    public CommandInfo setSyntax(String syntax) {
        this.syntax = syntax;
        return this;
    }

    public CommandInfo setNsfwOnly(boolean nsfwOnly) {
        this.nsfwOnly = nsfwOnly;
        return this;
    }

    public long getCooldown() {
        return cooldown;
    }

    public String getLabel() {
        return label;
    }

    public String getHelp() {
        return help;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public SyntaxBuilder getSyntaxBuilder() {
        return syntaxBuilder == null ? new SyntaxBuilder() : syntaxBuilder;
    }

    public String getSyntax() {
        return syntax == null ? "No syntax described" : syntax;
    }

    public boolean isNsfwOnly() {
        return nsfwOnly;
    }
}
