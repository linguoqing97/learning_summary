package com.jdh.fuhsi.portal.util;


import com.jdh.fuhsi.portal.exception.SystemException;
import com.jdh.log.LogTools;
import org.castor.core.util.Base64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * 图片验证码工具类
 * @author lym
 * @date 2020/2/25 10:38
 */
public class ImageCodeUtils {


    private ImageCodeUtils() {}

    /**
     * 生成图片验证码base64编码
     */
    public static String getImageCode(String key, int height, int width, String count) {
        RedisTools redisTools = SpringContextUtils.getBean("redisTools");
        // 备选字体
        String[] fontTypes = { "宋体", "黑体", "隶书", "楷体", "仿宋"};
        int fontTypesLength = fontTypes.length;

        // 备选颜色
        String[] strColor = { "红色", "蓝色"};
        Color[] objColor = { new Color(255, 0, 0), new Color(0, 0, 255) };

        // 创建随机类的实例
        SecureRandom random = new SecureRandom();
        // 随机选择颜色
        int sltColorIndex = random.nextInt(strColor.length);
        // 创建内存图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文
        Graphics g = image.getGraphics();

        g.setColor(getRandColor(230, 250));
        // 设定图像背景色(因为是做背景，所以偏淡)
        g.fillRect(0, 0, width, height);
        // 设定字体
        g.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));

        // 随机产生180条干扰线，使图象中的认证码不易被其它程序探测到
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(20);
            int yl = random.nextInt(20);
            g.setColor(getRandColor(100, 230));
            g.drawLine(x, y, x + xl, y + yl);
        }

        // 输出图片验证码
        String[] array = count.split("");
        for (int i = 0; i < array.length; i++) {
            Color color = new Color(0, 0, 0);
            int font = Font.PLAIN;
            int size = 18 + random.nextInt(3);
            // 符合坐标
            color = objColor[sltColorIndex];
            font = Font.BOLD;
            size = 24;

            g.setColor(color);
            g.setFont(new Font(fontTypes[random.nextInt(fontTypesLength)], font, size));
            g.drawString(array[i],  15 * i + random.nextInt(10), 20 + random.nextInt(10));
        }
        String formatName = "png";
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        boolean flag;
        try {
            flag = ImageIO.write(image, formatName, bytearrayoutputstream);
        } catch (IOException e) {
            LogTools.error("生成图片验证码失败:{}",e);
            //LYMTODO: 异常信息待国际标准化
            throw new SystemException("图片产生异常！");
        }
        ExceptionUtils.systemException(!flag, "图片产生异常！");

        //将图片流转换成base64编码
        char[] cs = Base64Encoder.encode(bytearrayoutputstream.toByteArray());
        StringBuilder imgBuffer = new StringBuilder();
        for (char c : cs) {
            imgBuffer.append(c);
        }
        //将验证码写入缓存
        redisTools.set(key, count, 120);
        return "data:image/jpg;base64,"+imgBuffer.toString();
    }


    /**
     * 产生随机颜色RGB随机值在fc到bc之间的颜色
     */
    private static Color getRandColor(int fc, int bc) {
        // 创建一个随机数生成器类
        SecureRandom random = new SecureRandom();
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

}
