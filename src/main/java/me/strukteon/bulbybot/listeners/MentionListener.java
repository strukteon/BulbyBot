/*
 * Created by Elias on 01.09.18 21:22
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.listeners;

import me.strukteon.bulbybot.core.sql.GuildSQL;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Settings;
import me.strukteon.bulbybot.utils.Static;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MentionListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getMessage().getMentionedMembers().size() == 0 || !event.getMessage().getContentDisplay().startsWith("@") || !event.getMessage().getMentionedMembers().get(0).getUser().getId().equals(event.getJDA().getSelfUser().getId()) || event.getAuthor().isBot())
            return;

        String prefix = Settings.INSTANCE.prefix;
        if (event.isFromType(ChannelType.TEXT))
            prefix = GuildSQL.fromGuild(event.getGuild()).getPrefix();

        EmbedBuilder eb = ChatTools.INFO(event.getAuthor())
                .setAuthor("Introducing... me!", Static.PLACEHOLDER_URL, event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription("My Prefix: **" + prefix + "**\nMy Website: **Coming soon!**\nVote for me on: **[discordbots.org](https://discordbots.org/bot/481221667813589027/vote)**\nOur GitHub repo: [github.com](https://github.com/strukteon/BulbyBot)\n\nMy Developers are: **black_wolf**#6549 & **strukteon**#2018\nPublished on: **Not yet :wink: (Closed Beta)**\nCreated by: **strukteon** on 22.8.2018");
        event.getChannel().sendMessage(eb.build()).queue();

    }

}
