package me.strukteon.bulbybot.utils.image;
/*
    Created by nils on 11.08.2018 at 01:30.
    
    (c) nils 2018
*/

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

public class ImageTools {

    public static BufferedImage scale(BufferedImage source, int width, int height){
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance((double)width/source.getWidth(),(double)height/source.getHeight());
        g.drawRenderedImage(source, at);
        return scaled;
    }

    public static Image makeColorTransparent(final BufferedImage im, final Color color)
    {
        final ImageFilter filter = new RGBImageFilter()
        {
            // the color we are looking for (white)... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0x00000000;

            public final int filterRGB(final int x, final int y, final int rgb)
            {
                if ((rgb | 0xFF000000) == markerRGB)
                {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                }
                else
                {
                    // nothing to do
                    return rgb;
                }
            }
        };

        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
}
