package me.strukteon.bulbybot.commands.general;
/*
    Created by nils on 28.08.2018 at 21:27.
    
    (c) nils 2018
*/

import com.sun.management.OperatingSystemMXBean;
import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.BulbyBot;
import me.strukteon.bulbybot.core.sql.UserSQL;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

public class Stats implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        EmbedBuilder eb = ChatTools.INFO(author);
        JDA.ShardInfo shardInfo = event.getJDA().getShardInfo();
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hardware = si.getHardware();
        GlobalMemory memory = hardware.getMemory();
        CentralProcessor processor = hardware.getProcessor();
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        DecimalFormat df = new DecimalFormat("###.##");

        eb.setDescription(String.format("Online for: **%s**\nGuilds: **%s**\nRegistered users: **%s**\nCPU usage: **%s%%**, **%s%%** available\nRAM usage: **%sMB**, **%sGB** available\nShard: **%s** / **%s**",
                onlineTime(System.currentTimeMillis() - BulbyBot.getStartTime()),
                BulbyBot.getShardManager().getGuilds().size(),
                UserSQL.getTotalUserSize(),
                df.format(operatingSystemMXBean.getProcessCpuLoad()*100), df.format((1.0 - processor.getSystemCpuLoad())*100),
                df.format((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Math.pow(1024, 2)), df.format(memory.getAvailable() / Math.pow(1024, 3)),
                shardInfo.getShardId()+1, shardInfo.getShardTotal()));
        System.out.println(System.currentTimeMillis() - BulbyBot.getStartTime());

        channel.sendMessage(eb.build()).queue();
    }

    public String onlineTime(long time){
        int days = (int)(time / 24 / 60 / 60 / 1000);
        int hours = (int)((time - days * 86400000) / 60 / 60 / 1000);
        int mins = (int)((time - days * 86400000 - hours * 3600000) / 60 / 1000);
        int secs = (int)((time - days * 86400000 - hours * 3600000 - mins * 60000) / 1000);

        return String.format("%sd, %sh, %smin, %ss", days, hours, mins, secs);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("stats")
                .setHelp("Shows some statistics of the bot");
    }
}
