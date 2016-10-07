/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamengine.utils;

//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.image.BufferedImage;

import android.graphics.Bitmap;

/**
 *
 * @author dusan_cvetkovic
 */
public class ImageUtils {
//    public static BufferedImage toBufferedImage(Image img) {
//        if (img instanceof BufferedImage) {
//            return (BufferedImage) img;
//        }
//
//        // Create a buffered image with transparency
//        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//
//        // Draw the image on to the buffered image
//        Graphics2D bGr = bimage.createGraphics();
//        bGr.drawImage(img, 0, 0, null);
//        bGr.dispose();
//
//        // Return the buffered image
//        return bimage;
//    }
    
    public static Bitmap[] spriteToImagesArray(Bitmap sprite, int numOfImages, int widthOfIndividualImg) {
//        BufferedImage biSprite = toBufferedImage(sprite);
        Bitmap animation[] = new Bitmap[numOfImages];
        for (int i = 1; i < numOfImages+1; i++) {
            animation[i - 1] = Bitmap.createBitmap(sprite,(i-1)*widthOfIndividualImg, 0, widthOfIndividualImg, widthOfIndividualImg);
        }
        return animation;
    }
}
