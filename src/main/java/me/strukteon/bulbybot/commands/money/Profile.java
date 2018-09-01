package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 23.08.2018 at 00:03.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.BulbyBot;
import me.strukteon.bulbybot.core.sql.PatreonSQL;
import me.strukteon.bulbybot.core.sql.inventory.InventorySQL;
import me.strukteon.bulbybot.core.threading.RenderItem;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.image.ProfileImage;
import me.strukteon.bulbybot.utils.items.Item;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Profile implements ExtendedCommand {

    @Override
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        User user;
        if (event.isGuild())
            user = syntax.getAsMember("user") == null ? author : syntax.getAsMember("user").getUser();
        else user = author;

        Member m = null;
        if (event.isGuild())
            m = syntax.getAsMember("user") == null ? event.getMember() : syntax.getAsMember("user");
        Member member = m; // ffs java compiler

        Register.isRegistered(event, user, userSQL -> {
            InventorySQL inventorySQL = InventorySQL.fromUser(user);

            ProfileImage.UserInfo ui = new ProfileImage.UserInfo();
            ui.username = user.getName();
            ui.discrim = user.getDiscriminator();

            userSQL.getBadges().forEach(badge -> {
                ui.badges.add(Profile.class.getResourceAsStream(badge.getResourcePath()));
            });

            if (event.isGuild()) {
                switch (member.getOnlineStatus()) {
                    default:
                        ui.onlineState = Color.decode("#00bdff");
                        break;
                    case IDLE:
                        ui.onlineState = Color.decode("#ffa300");
                        break;
                    case DO_NOT_DISTURB:
                        ui.onlineState = Color.decode("#ff0000");
                        break;
                    case OFFLINE:
                        ui.onlineState = Color.decode("#7c7c7c");
                        break;
                }
            } else
                ui.onlineState = Color.decode("#00bdff");
            ui.money = userSQL.getMoney();
            ui.bio = userSQL.getBio();
            ui.xp = userSQL.getXp();
            ui.isPatron = PatreonSQL.isPatron(user);
            ui.rank = userSQL.getRank();
            ui.rankTotal = userSQL.getRankSize();

            // IMAGES

            try {
                ui.userImage = new URL(user.getEffectiveAvatarUrl()).openStream();
            } catch (IOException ignored) { }
            String imagePath = (String) inventorySQL.getUsedItem(Item.Type.BACKGROUND).getItem().getAdditionalInfo();
            ui.bgImage = this.getClass().getResourceAsStream(imagePath);
            if (ui.isPatron)
                ui.patronImage = this.getClass().getResourceAsStream("/images/badges/patron-popdown.png");

            ChatTools.CheckList checkList = new ChatTools.CheckList("Queueing", "Rendering", "Uploading");
            checkList.setMode(0, ChatTools.CheckList.MODE_LOADING);

            channel.sendMessage(checkList.getEmbedBuilder().build()).queue(message -> {

                RenderItem.OnFinishInterface onFinish = (file, renderTime, queueTime) -> {
                    checkList.setMode(1, ChatTools.CheckList.MODE_FINISHED);
                    checkList.setMode(2, ChatTools.CheckList.MODE_LOADING);
                    message.editMessage(checkList.getEmbedBuilder().build()).queue();

                    channel.sendFile(file).queue(ignored -> {
                        file.delete();
                        checkList.setMode(2, ChatTools.CheckList.MODE_FINISHED);
                        message.editMessage(checkList.getEmbedBuilder().build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
                    });
                };

                RenderItem.OnStartInterface onStart = () -> {
                    checkList.setMode(0, ChatTools.CheckList.MODE_FINISHED);
                    checkList.setMode(1, ChatTools.CheckList.MODE_LOADING);
                };

                boolean success;
                if (ui.isPatron)
                    success = BulbyBot.premiumRenderThread.offerRenderItem(new RenderItem(new ProfileImage(ui), onFinish, onStart));
                else
                    success = BulbyBot.defaultRenderThread.offerRenderItem(new RenderItem(new ProfileImage(ui), onFinish, onStart));

                if (!success) {
                    checkList.setMode(0, ChatTools.CheckList.MODE_CANCELED);
                    checkList.setState("Sorry, but it seems like the queue is full. Please try again later or consider buying premium to get access to an exclusive premium-only queue.");
                    message.editMessage(checkList.getEmbedBuilder().build()).queue();
                }

            });

        });
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("profile")
                .setHelp("Shows you a neat image of your profile")
                .setAliases("p")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                                .addOptionalElement("user", SyntaxElementType.MEMBER)
                );
    }
}
