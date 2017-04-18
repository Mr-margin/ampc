package ampc.com.gistone.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
* @ClassName: CaptchaUtil 
* @Description: 关于验证码的工具类
* @author 无名
* @date 2016-5-7 上午8:33:08 
* @version 1.0
 */
public final class CaptchaUtil
{
    private CaptchaUtil(){}
    
    /*
     * 随机字符字典
     */
    private static final char[] CHARS = { '2', '3', '4', '5', '6', '7', '8',
        '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
        'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    
    /*
     * 随机数
     */
    private static Random random = new Random();
    
    /*
     * 获取6位随机数
     */
    private static String getRandomString()
    {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < 4; i++)
        {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }
    
    /*
     * 获取随机数颜色
     */
    private static Color getRandomColor()
    {
        return new Color(20 + random.nextInt(110), 20 + random  
                .nextInt(110), 20 + random.nextInt(110));
    }
    
    private static Color getRandColor(int fc, int bc) {  
        Random random = new Random();  
        if (fc > 255)  
            fc = 255;  
        if (bc > 255)  
            bc = 255;  
        int r = fc + random.nextInt(bc - fc);  
        int g = fc + random.nextInt(bc - fc);  
        int b = fc + random.nextInt(bc - fc);  
        return new Color(r, g, b);  
    }  
    /*
     * 返回某颜色的反色
     */
    private static Color getReverseColor(Color c)
    {
        return new Color(255 - c.getRed(), 255 - c.getGreen(),
                255 - c.getBlue());
    }
    
    public static void outputCaptcha(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {

        response.setContentType("image/jpeg");

       

        int width = 100;
        int height = 30;

        Color color = getRandomColor();
        Color reverse = getReverseColor(color);

        BufferedImage bi = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);  
        // 获取图形上下文    
        // 生成随机类  
        Random random = new Random();  
        // 设定背景色  
        g.setColor(getRandColor(200, 250));  
        g.fillRect(0, 0, width, height);  
        // 设定字体  
        g.setFont(new Font("Times New Roman", Font.PLAIN, 28));  
        // 随机产生255条干扰线，使图象中的认证码不易被其它程序探测到  
        g.setColor(getRandColor(160, 200));  
//        for (int i = 0; i < 155; i++) {  
//            int x = random.nextInt(width);  
//            int y = random.nextInt(height);  
//            int xl = random.nextInt(12);  
//            int yl = random.nextInt(12);  
//            g.drawLine(x, y, x + xl, y + yl);  
//        }  
        // 取随机产生的认证码(6位数字)  
        StringBuffer sRand = new StringBuffer();    
        for (int i = 0; i < 4; i++) {  
            String rand = String.valueOf(CHARS[random.nextInt(CHARS.length-1)]);//从字符数组中随机产生一个字符  
            sRand.append(rand);   
            // 将认证码显示到图象中  
            g.setColor(new Color(30 + random.nextInt(150), 30 + random.nextInt(150), 30 + random.nextInt(150)));  
            // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成  
            g.drawString(rand, 22 * i + 6, 24);  
        }
        g.setStroke(new BasicStroke(3));
        // 赋值验证码  
        String str = sRand.toString();  
       
        request.getSession(true).setAttribute("randomString", str);
        // 图象生效  
        g.dispose(); 

        // 转成JPEG格式
        ServletOutputStream out = response.getOutputStream();
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(bi);
        out.flush();
    }
}