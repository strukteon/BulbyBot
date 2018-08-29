package me.strukteon.bulbybot.utils;
/*
    Created by nils on 11.08.2018 at 03:25.
    
    (c) nils 2018
*/

public class LevelSystem {

    public static int getXp(int msgLen){
        return Math.max(1, msgLen/5);
    }

    public static int getLevel(long xp){
        int level = 1;
        float levelMultipl = 1.1f;
        while (xp >= 50 * level * levelMultipl){
            xp -= 50 * level * levelMultipl;
            levelMultipl *= 1.1;
            level++;
        }
        return level;
    }

    public static long getXpToNextLevel(int level){
        return (long) (50 * (level) * Math.pow(1.1, level)) + 1;
    }

    public static long getRemainingXp(long xp){
        int level = 1;
        float levelMultipl = 1.1f;
        while (xp >= 50 * level * levelMultipl){
            xp -= 50 * level * levelMultipl;
            levelMultipl *= 1.1;
            level++;
        }
        return xp;
    }

}
