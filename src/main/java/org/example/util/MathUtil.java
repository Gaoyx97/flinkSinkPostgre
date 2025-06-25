package org.example.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {
    /**
     * 计算两个数的除法（保留指定位数小数）
     * @param a 被除数
     * @param b 除数
     * @param scale 保留小数位数
     * @return 结果字符串（自动补零）
     */
    public static Double divide(int a, int b, int scale) {
        BigDecimal percent = new BigDecimal(a)
                .divide(new BigDecimal(b), scale, RoundingMode.HALF_UP);
        return percent.doubleValue();
    }
    /**
     * 计算百分比（保留2位小数）
     */
    public static Double calculatePercentage(int used, int total) {
        BigDecimal percent = new BigDecimal(used)
                .divide(new BigDecimal(total), 4, RoundingMode.HALF_UP) // 中间结果多保留几位
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP); // 最终结果保留2位

        return percent.doubleValue();
    }

    public static Double reserveDecimal(Double number,int digit){
        BigDecimal percent = new BigDecimal(number);
        percent = percent.setScale(digit, RoundingMode.HALF_UP);
        return percent.doubleValue();
    }

}
