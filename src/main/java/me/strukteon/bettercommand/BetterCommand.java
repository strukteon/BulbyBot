package me.strukteon.bettercommand;
/*
    Created by nils on 06.07.2018 at 15:46.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.command.BaseCommand;
import me.strukteon.bettercommand.command.ErrorHandler;
import me.strukteon.bettercommand.command.Loader;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class BetterCommand {
    public static String VALID_PREFIX = "[^\\s][^\t\n\r]+";

    private Object jda; // or shardmanager
    private String defaultPrefix;

    private boolean commandIgnoreCase;
    private boolean deleteUserMsg;
    private long cooldown;

    private Loader.Prefix prefixLoader;
    private Loader.Blacklisted blacklistedLoader;
    private CommandListener commandListener;
    private ErrorHandler errorHandler;

    private List<CommandSection> commandSections;
    private List<BaseCommand> unassignedCommands;

    private boolean active = false;

    public BetterCommand(@Nonnull String defaultPrefix, boolean ignoreCase){
        this.defaultPrefix = defaultPrefix;
        this.commandIgnoreCase = ignoreCase;
        this.cooldown = 0;
        this.commandSections = new ArrayList<>();
        this.unassignedCommands = new ArrayList<>();
        this.commandListener = new CommandListener(this);
        this.prefixLoader = new Loader.Prefix() {
            @Override
            public String getGuildPrefix(long guildid) {
                return null;
            }
        };
        this.errorHandler = new DefaultErrorHandler();
    }

    public BetterCommand useShardManager(@Nonnull ShardManager manager){
        this.jda = manager;
        return this;
    }

    public BetterCommand useJDA(@Nonnull JDA jda){
        this.jda = jda;
        return this;
    }

    public BetterCommand useDefaultHelpMessage(@Nonnull String label, @Nonnull String... aliases){
        unassignedCommands.add(new DefaultHelpCommand(label, aliases));
        return this;
    }

    public BetterCommand useDefaultHelpMessage(@Nonnull String label, @Nonnull Color color, @Nonnull String... aliases){
        unassignedCommands.add(new DefaultHelpCommand(label, color, aliases));
        return this;
    }

    public BetterCommand setIgnoreCase(boolean ignoreCase){
        this.commandIgnoreCase = ignoreCase;
        return this;
    }

    public BetterCommand setDeleteUserMessage(boolean deleteUserMsg) {
        this.deleteUserMsg = deleteUserMsg;
        return this;
    }

    public BetterCommand addCommandSection(CommandSection commandSection){
        commandSections.add(commandSection);
        return this;
    }

    public BetterCommand addCommandSection(String sectionName, BaseCommand... commands){
        return addCommandSection(new CommandSection(sectionName, commands));
    }

    public BetterCommand addCommandSection(String sectionName, Collection<BaseCommand> commands){
        return addCommandSection(new CommandSection(sectionName, commands));
    }

    public BetterCommand setCooldown(long millis){
        this.cooldown = millis;
        return this;
    }

    public BetterCommand setPrefixLoader(Loader.Prefix prefixLoader) {
        this.prefixLoader = prefixLoader;
        return this;
    }

    public BetterCommand setBlacklistedLoader(Loader.Blacklisted blacklistedLoader) {
        this.blacklistedLoader = blacklistedLoader;
        return this;
    }

    public BetterCommand setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public BetterCommand enable(){
        if (this.active)
            throw new BetterCommandException("This instance is already active");
        if (jda == null)
            throw new BetterCommandException("This instance's jda was not set");
        if (jda instanceof ShardManager)
            ((ShardManager) jda).addEventListener(this.commandListener);
        else
            ((JDA) jda).addEventListener(this.commandListener);
        this.active = true;
        return this;
    }

    public BetterCommand disable(){
        if (this.active)
            throw new BetterCommandException("This instance is not active");
        if (jda instanceof ShardManager)
            ((ShardManager) jda).removeEventListener(this.commandListener);
        else
            ((JDA) jda).removeEventListener(this.commandListener);
        this.active = false;
        return this;
    }


    public boolean isActive() {
        return active;
    }

    protected long getCooldown() {
        return cooldown;
    }

    protected String getDefaultPrefix() {
        return defaultPrefix;
    }

    protected Loader.Prefix getPrefixLoader() {
        return prefixLoader == null ? new DefaultLoader.Prefix() : prefixLoader;
    }

    protected Loader.Blacklisted getBlacklistedLoader() {
        return blacklistedLoader == null ? new DefaultLoader.Blacklisted() : blacklistedLoader;
    }

    protected String getPrefix(long guildId){
        String guildPrefix = prefixLoader.getGuildPrefix(guildId);
        if (guildPrefix != null && Pattern.quote(guildPrefix).matches(VALID_PREFIX))
            return guildPrefix;
        return defaultPrefix;
    }

    protected ErrorHandler getErrorHandler(){
        return errorHandler;
    }

    public List<CommandSection> getCommandSections() {
        return commandSections;
    }

    public List<BaseCommand> getUnassignedCommands() {
        return unassignedCommands;
    }

    public List<BaseCommand> getCommands() {
        List<BaseCommand> commands = new ArrayList<>(unassignedCommands);
        commandSections.forEach(commandSection -> commands.addAll(commandSection.getCommands()));
        return commands;
    }

    public boolean getCommandIgnoreCase(){
        return commandIgnoreCase;
    }

    public boolean getDeleteUserMsg() {
        return deleteUserMsg;
    }
}
