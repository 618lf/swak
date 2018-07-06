package com.swak.security.web.captcha.builder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.swak.security.web.captcha.Captcha;

/**
 * 验证码创建者实体类A。该类是一个加干扰线白底随机颜色字符的通用验证码实体类。
 *
 * @author bing <503718696@qq.com>
 * @date 2016-5-15 21:08:41
 * @version v0.1
 */
public class ABuilder extends AbstractBuilder {

    private String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";//用于生产的母字符串

    @Override
    public Captcha generateCaptcha() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            sb.append(randString.charAt(random.nextInt(randString.length())));
        }
        return new Captcha(sb.toString());
    }

    @Override
    public Image generateImage(Captcha code) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = bi.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(ColorUtil.randomColor());
        //绘制随机字符
        drawCodeString(g, code.getCode());
        g.dispose();
        return bi;
    }

    /**
     * 绘制字符串。
     *
     * @param g
     * @param code 随机字符串
     * @param i
     * @return
     */
    private void drawCodeString(Graphics g, String code) {
        g.setFont(font);
        for (int i = 0; i < codeLength; i++) {
            g.translate(random.nextInt(10), random.nextInt(4));
            g.setColor(ColorUtil.randomColor());
            g.drawString(String.valueOf(code.charAt(i)), 13 * i, 16);
        }
    }
}