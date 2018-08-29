package me.strukteon.bettercommand;/*
    Created by nils on 07.02.2018 at 02:21.
    
    (c) nils 2018
*/

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommandTools {

    public static boolean botCanModify(Role r){
        return r.getPosition() < r.getGuild().getMemberById(r.getJDA().getSelfUser().getId()).getRoles().get(0).getPosition();
    }

    public static List<Permission> getMissingPermissions(Collection<Permission> available, List<Permission> required){
        List<Permission> missing = new ArrayList<>(required);
        missing.removeAll(available);
        return missing;
    }

    public static boolean isId(String toCheck){
        return toCheck.matches("\\d{18}");
    }

    public static boolean isMention(String toCheck){
        return toCheck.matches("<@(!)?\\d{18}>");
    }

    public static boolean isChannelMention(String toCheck){
        return toCheck.matches("<#(!)?\\d{18}>");
    }

    public static boolean isRoleMention(String toCheck){
        return toCheck.matches("<@&\\d{18}>");
    }

    public static String mentionToId(String mention){
        return mention.replaceAll("<@(!)?|>", "");
    }

    public static String channelMentionToId(String mention){
        return mention.replaceAll("<#(!)?|>", "");
    }

    public static String roleToId(String mention){
        return mention.replaceAll("<#&|>", "");
    }

    public static String permsCheck(Permission[] required, List<Permission> available){
        return permsCheck(Arrays.asList(required), available);
    }

    public static String permsCheck(List<Permission> required, List<Permission> available){
        StringBuilder out = new StringBuilder();
        if (required.size() == 0)
            out.append("*None*");
        for (Permission p : required){
            if (out.length() > 0)
                out.append("\n");
            if (available.contains(p) || available.contains(Permission.ADMINISTRATOR))
                out.append(":white_check_mark:");
            else
                out.append(":x:");
            out.append(" *" + p.getName() + "*");
        }
        return out.toString();
    }

}
