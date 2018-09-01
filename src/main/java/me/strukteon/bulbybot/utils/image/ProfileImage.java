package me.strukteon.bulbybot.utils.image;
/*
    Created by nils on 10.08.2018 at 20:18.
    
    (c) nils 2018
*/

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;
import me.strukteon.bulbybot.core.CLI;
import me.strukteon.bulbybot.utils.LevelSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileImage implements Renderable {
    private UserInfo userInfo;

    private Composite defaultComp;

    private Graphics2D g2d;
    private BufferedImage main;
    private BufferedImage background;
    private BufferedImage avatar;
    private BufferedImage patronImage;
    private List<BufferedImage> badges = new ArrayList<>();

    private boolean isGif;

    public static void main(String[] args) {
        LevelSystem.getLevel(Long.MAX_VALUE);
        UserInfo ui = new UserInfo();
        try {
            ui.bgImage = new FileInputStream(new File("D:\\Downloads\\discord\\bg-stars.jpg"));
            ui.userImage = new FileInputStream(new File("D:\\Downloads\\discord\\user_pic.png"));
            ui.patronImage = new FileInputStream(new File("D:\\Downloads\\discord\\patron_popdown.png"));
            ui.badges.add(new FileInputStream(new File("D:\\Downloads\\discord\\badge-dev.png")));
            ui.badges.add(new FileInputStream(new File("D:\\Downloads\\discord\\badge-staff.png")));
            ui.badges.add(new FileInputStream(new File("D:\\Downloads\\discord\\badge-beta.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //ui.userImage = new File("D:\\Downloads\\discord\\a.gif");
        ui.onlineState = Color.decode("#00bdff");
        ui.username = "strukteon";
        ui.discrim = "7237";
        ui.isPatron = true;
        ui.bio = "none";
        ui.xp = 18653;
        ui.money = 16789;
        System.out.println(new ProfileImage(ui).getFile());
    }

    public ProfileImage(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    @Override
    public void render(){

        String moneyText = (userInfo.money > 9999999 ? "9999999+" : userInfo.money) + " $";
        try {
            userInfo.badges.forEach(file -> {
                try {
                    badges.add(ImageTools.scale(ImageIO.read(file), 50, 50));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            background = ImageTools.scale(ImageIO.read(userInfo.bgImage), 1280, 720);
            avatar = ImageTools.scale(ImageIO.read(userInfo.userImage), 256, 256);
            if (userInfo.isPatron)
                patronImage = ImageIO.read(userInfo.patronImage);
            main = new BufferedImage(1270, 720, BufferedImage.TYPE_INT_ARGB);
            g2d = main.createGraphics();
            defaultComp = g2d.getComposite();
        } catch (IOException e) {
            e.printStackTrace();
        }
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        g2d.drawImage(background, 0, 0, null);

        if (userInfo.isPatron){
            g2d.drawImage(patronImage, 1280 - patronImage.getWidth(), 0, null);
        }

        Font discordia = null;
        try {
            discordia = Font.createFont(Font.TRUETYPE_FONT, ProfileImage.class.getResourceAsStream("/res/discordia.otf")).deriveFont(70f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        // header
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setPaint(Color.decode("#000000"));
        g2d.fillRect(0, 112, 1280, 175);

        // bio header
        g2d.fillRect(0, 425, 1280, 256);


        // username
        g2d.setFont(discordia);
        g2d.setComposite(defaultComp);
        g2d.setPaint(Color.decode("#ffffff"));
        int nameLen = userInfo.username.length();
        while (g2d.getFontMetrics().stringWidth(userInfo.username.substring(0, nameLen) + "...#" + userInfo.discrim) > 700)
            nameLen--;
        g2d.drawString(userInfo.username.substring(0, nameLen) + (nameLen == userInfo.username.length() ? "" : "...") + "#" + userInfo.discrim, 500, 185);

        // below username
        g2d.setFont(discordia.deriveFont(48f));
        g2d.drawString("Level " + LevelSystem.getLevel(userInfo.xp), 550, 250);
        g2d.drawString(moneyText, 1075 - g2d.getFontMetrics().stringWidth(moneyText), 250);

        // bio & co
        g2d.drawString("Bio |", 150, 510);

        g2d.drawString("XP |", 150 + g2d.getFontMetrics().stringWidth("Bio |") - g2d.getFontMetrics().stringWidth("XP |"), 600);

        int rankX = 1280 - 150 - g2d.getFontMetrics().stringWidth("| Rank");
        g2d.drawString("| Rank", rankX, 600);

        int repX = 1280 - 150 - g2d.getFontMetrics().stringWidth("| Rep");
        g2d.drawString("| Rep", repX, 510);

        g2d.setPaint(Color.decode("#9e9e9e"));
        g2d.drawString(" " + userInfo.bio, 150 + g2d.getFontMetrics().stringWidth("Bio |"), 510);

        g2d.drawString(" " + displayed(LevelSystem.getRemainingXp(userInfo.xp)) + " / " + displayed(LevelSystem.getXpToNextLevel(LevelSystem.getLevel(userInfo.xp))), 150 + g2d.getFontMetrics().stringWidth("Bio |"), 600);

        String rankText = displayed(userInfo.rank) + " / " + displayed(userInfo.rankTotal) + " ";
        g2d.drawString(rankText, rankX - g2d.getFontMetrics().stringWidth(rankText), 600);

        String repText = displayed(userInfo.reputation) + " ";
        g2d.drawString(repText, repX - g2d.getFontMetrics().stringWidth(repText), 510);

        g2d.setPaint(userInfo.onlineState);
        g2d.fillRect(150, 70, 266, 266);

        if (!isGif)
            g2d.drawImage(avatar, 150, 70, null);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setPaint(Color.decode("#000000"));
        g2d.fillRect(150, 70 + 266, 266, 64);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        for (int i = 0; i < badges.size(); i++){
            int baseX = 150;
            int multi = i >= Math.ceil(badges.size()/2) ? 1 : -1;
            if (badges.size() % 2 == 0) {
                g2d.drawImage(ImageTools.makeColorTransparent(badges.get(i), Color.decode("#00000000")), baseX + (266/2) - badges.size()/2*50 + i * 50, 70 + 266 + 7, null);
            } else {
                g2d.drawImage(ImageTools.makeColorTransparent(badges.get(i), Color.decode("#00000000")), baseX + 266/2 - (badges.size()+1)/2*50 + 25 + i * 50, 70 + 266 + 7, null);
            }
        }

    }

    private String displayed(long l){
        return l > 9999999 ? "9999999+" : l + "";
    }

    @Override
    public File getFile(){
        File file = null;
        try {
            CLI.info("called getFile()");
            //file = isGif ? File.createTempFile("profile-", ".gif") : File.createTempFile("profile-", ".png");
            file = isGif ? File.createTempFile("profile-", ".gif") : File.createTempFile("profile-", ".png");
            if (isGif){
                AnimatedGifEncoder encoder = new AnimatedGifEncoder();
                GifDecoder decoder = new GifDecoder();
                decoder.read(userInfo.userImage);
                encoder.start(new FileOutputStream(file));

                encoder.setRepeat(0);
                encoder.setDelay(decoder.delay * (decoder.getFrameCount()/10));
                encoder.setDispose(decoder.dispose);
                encoder.setQuality(1);
                encoder.setSize(1280, 720);

                for (int i = 0; i < decoder.getFrameCount(); i += decoder.getFrameCount()/10) {
                    System.out.println(decoder.getFrameCount() + " / " + (i + decoder.getFrameCount()/10));

                    g2d.drawImage(ImageTools.scale(decoder.getFrame(i), 256, 256), 150, 70, null);
                    encoder.addFrame(main);
                }

                encoder.finish();
            } else
                ImageIO.write(main, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }


    public static class UserInfo {
        public String username;
        public String discrim;
        public boolean isPatron;
        public InputStream userImage;
        public InputStream bgImage;
        public InputStream patronImage;
        public String bio;
        public long xp;
        public int level;
        public long money;
        public Color onlineState;
        public int reputation;

        public List<InputStream> badges = new ArrayList<>();

        public int rank;
        public int rankTotal;
    }

}
