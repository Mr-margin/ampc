package ampc.com.gistone.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CastNumUtil {
	/**保留几位有效数字 
     * @param oldDouble 
     * @param scale 
     * @return 
     */  
    public static double significand(double oldDouble, int scale) {  
        if (scale < 0) {  
            throw new IllegalArgumentException("scale指定的精度为非负值");  
        }  
        /** 
         * RoundingMode：舍入模式 
         * UP：远离零方向舍入的舍入模式； 
         * DOWN：向零方向舍入的舍入模式； 
         * CEILING： 向正无限大方向舍入的舍入模式； 
         * FLOOR：向负无限大方向舍入的舍入模式； 
         * HALF_DOWN：向最接近数字方向舍入的舍入模式，如果与两个相邻数字的距离相等，则向下舍入； 
         * HALF_UP：向最接近数字方向舍入的舍入模式，如果与两个相邻数字的距离相等，则向上舍入； 
         * HALF_EVEN：向最接近数字方向舍入的舍入模式，如果与两个相邻数字的距离相等，则向相邻的偶数舍入;(在重复进行一系列计算时,此舍入模式可以将累加错误减到最小) 
         * UNNECESSARY：用于断言请求的操作具有精确结果的舍入模式，因此不需要舍入。 
         */  
        RoundingMode rMode =null;  
        //rMode=RoundingMode.FLOOR;  
        //下面这种情况，其实和FLOOR一样的。  
        if(oldDouble>0){  
            rMode=RoundingMode.DOWN;  
        }else{  
            rMode=RoundingMode.UP;  
        }  
        //此处的scale表示的是，几位有效位数  
        BigDecimal b = new BigDecimal(Double.toString(oldDouble),new MathContext(scale,rMode));  
        return b.doubleValue();  
    }  
    /**小数点之后保留几位小数(此处，我们用BigDecimal提供的（除以div）方法实现) 
     * @param oldDouble 
     * @param scale 
     * @return 
     */  
    public static double decimal(double oldDouble, int scale) {  
        if (scale < 0) {  
            throw new IllegalArgumentException(  
                    "The scale must be a positive integer or zero");  
        }  
        BigDecimal b = new BigDecimal(Double.toString(oldDouble));  
        BigDecimal one = new BigDecimal("1");  
        //return b.divide(one, scale, BigDecimal.ROUND_FLOOR).doubleValue();  
        if(oldDouble>0){  
            //此处的scale表示的是，小数点之后的精度。  
            return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
        }else{  
            return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
        }  
    } 
}
