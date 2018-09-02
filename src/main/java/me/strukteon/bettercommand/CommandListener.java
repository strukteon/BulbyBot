import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;

public class CommandListener extends ListenerAdapter {

    private BetterCommand betterCommand;
    private HashMap<String, Long> cooldowns; // user id, last timestamp

    protected CommandListener(BetterCommand instance){
        this.betterCommand = instance;
        this.cooldowns = new HashMap<>();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        CommandEvent commandEvent = new CommandEvent(event, betterCommand, event.isFromType(ChannelType.TEXT));
        String prefix;
        if (event.getChannelType().isGuild()) {
            prefix = betterCommand.getPrefix(event.getGuild().getIdLong());
        } else {
            prefix = betterCommand.getDefaultPrefix();
        }
        boolean startsWithPrefix = event.getMessage().getContentRaw().startsWith(prefix);
        if (startsWithPrefix || event.getChannelType().equals(ChannelType.PRIVATE)) {
            commandEvent.setUsedPrefix(startsWithPrefix ? prefix : "");
            if (!betterCommand.getBlacklistedLoader().isChannelBlacklisted(event.getChannel().getIdLong()) || !betterCommand.getBlacklistedLoader().isUserBlacklisted(event.getAuthor().getIdLong()) || !event.getAuthor().isBot()) {
                if (betterCommand.getCooldown() != 0)
                    if (cooldowns.containsKey(event.getAuthor().getId()) && cooldowns.get(event.getAuthor().getId()) + betterCommand.getCooldown() > System.currentTimeMillis())
                        return;
                String[] splitted = event.getMessage().getContentRaw().replaceFirst(prefix, "").split(" ");
                String command = betterCommand.getCommandIgnoreCase() ? splitted[0].toLowerCase() : splitted[0];
                String[] args = splitted.length > 1 ? Arrays.copyOfRange(splitted, 1, splitted.length) : new String[0];
                for (BaseCommand c : betterCommand.getCommands())
                    if (c.getCommandInfo().getLabel().equals(command) || c.getCommandInfo().getAliases().contains(command)) {
                        if (c.getCommandInfo().getCooldown() != 0)
                            if (cooldowns.containsKey(event.getAuthor().getId()) && cooldowns.get(event.getAuthor().getId()) + c.getCommandInfo().getCooldown() > System.currentTimeMillis())
                                return;
                        if (!(c.getPermissionManager().getLimitedUsers().size() == 0 || c.getPermissionManager().getLimitedUsers().contains(event.getAuthor().getId()))) {
                            betterCommand.getErrorHandler().notInUserlist(commandEvent, c);
                            return;
                        }
                        if (c instanceof GuildCommand) {
                            Member author = event.getMember();
                            Member self = event.getGuild().getMember(event.getJDA().getSelfUser());
                            boolean success;
                            if (!(success = (author.hasPermission(Permission.ADMINISTRATOR) ||
                                    event.getGuild().getOwner().getUser().getId().equals(author.getUser().getId()) ||
                                    author.hasPermission(c.getPermissionManager().getRequiredUserPerms()))))
                                betterCommand.getErrorHandler().missingUserPermissions(commandEvent,
                                        CommandTools.getMissingPermissions(author.getPermissions(event.getTextChannel()), c.getPermissionManager().getRequiredUserPerms()),
                                        c);

                            if (!( self.hasPermission(Permission.ADMINISTRATOR) || self.hasPermission(c.getPermissionManager().getRequiredBotPerms()) )) {
                                success = false;
                                betterCommand.getErrorHandler().missingBotPermissions(commandEvent,
                                        CommandTools.getMissingPermissions(self.getPermissions(event.getTextChannel()), c.getPermissionManager().getRequiredBotPerms()),
                                        c);
                            }
                            if (!success)
                                return;
                        }
                        if (c instanceof ExtendedCommand) {
                            if (c.getCommandInfo().getSyntaxBuilder() == null) {
                                c.getCommandInfo().setSyntaxBuilder(new SyntaxBuilder());
                            }


                            c.getCommandInfo().getSyntaxBuilder().setErrorHandler(new SyntaxHandler() {
                                @Override
                                public boolean onException(SyntaxValidateException e) {
                                    return betterCommand.getErrorHandler().validateException(commandEvent, c, e);
                                }

                                @Override
                                public void onFinish(Syntax syntax) {
                                    try {
                                        ((ExtendedCommand) c).onExecute(commandEvent, syntax, event.getAuthor(), event.getChannel());
                                        if (c instanceof ExtendedGuildCommand)
                                            ((ExtendedGuildCommand) c).onExecute(commandEvent, syntax, event.getMember(), event.getTextChannel());
                                    } catch (Exception e){
                                        betterCommand.getErrorHandler().onException(commandEvent, c, e);
                                    }
                                }
                            }).build(c.getCommandInfo().getLabel(), args, event);
                        }
                        try {
                            if (c instanceof Command)
                                ((Command) c).onExecute(commandEvent, args, event.getAuthor(), event.getChannel());
                            if (c instanceof GuildCommand)
                                ((GuildCommand) c).onExecute(commandEvent, args, event.getMember(), event.getTextChannel());
                        } catch (Exception e){
                            betterCommand.getErrorHandler().onException(commandEvent, c, e);
                        }

                        if (betterCommand.getDeleteUserMsg() && event.getChannelType().equals(ChannelType.TEXT))
                            event.getMessage().delete().queue();
                    }
            }
        }
    }
}
