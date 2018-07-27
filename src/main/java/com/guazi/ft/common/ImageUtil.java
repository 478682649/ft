package com.guazi.ft.common;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 图片工具类
 *
 * @author shichunyang
 */
public class ImageUtil {

    private ImageUtil() {
    }

    private static final String CHARS = "1234567890";

    /**
     * 创建图片验证码
     *
     * @param out 验证码输出流
     * @return 验证码文字
     */
    public static String getImage(OutputStream out) {

        ImageIO.setUseCache(false);

        String capText = getText(4);

        BufferedImage bi = createImage(60, 30, capText);

        try {
            ImageIO.write(bi, "png", out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return capText;
    }

    /**
     * 随机字符的个数
     */
    private static String getText(int length) {

        char[] chars = CHARS.toCharArray();

        Random rand = new Random();

        StringBuffer text = new StringBuffer();

        for (int i = 0; i < length; i++) {
            text.append(chars[rand.nextInt(CHARS.length())]);
        }

        return text.toString();
    }

    /**
     * 获取颜色
     */
    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();

        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 创建图片缓冲区
     */
    private static BufferedImage createImage(int width, int height, String text) {

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = (Graphics2D) bi.getGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.GRAY);

        g2.fillRect(0, 0, width, height);

        Color c = getRandColor(200, 250);

        g2.setColor(c);

        g2.fillRect(0, 2, width, height - 4);

        g2.setColor(getRandColor(100, 160));

        int fontSize = height - 4;

        Font font = new Font("Algerian", Font.ITALIC, fontSize);

        g2.setFont(font);

        char[] chars = text.toCharArray();

        for (int i = 0; i < 4; i++) {
            //设置倾斜度
            g2.drawChars(chars, i, 1, ((width) / 5) * i + 5, height / 2 + fontSize / 2 - 5);
        }

        g2.dispose();

        return bi;
    }
}
