package com.like.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 用于Double类型的小数位格式化
 */
public class DoubleFormatUtils {
    /**
     * 0~2位小数
     */
    public static int DECIMAL_TYPE_0_2 = 0;
    /**
     * 1~2位小数
     */
    public static int DECIMAL_TYPE_1_2 = 1;
    /**
     * 固定2位小数
     */
    public static int DECIMAL_TYPE_2_2 = 2;
    /**
     * 0~1位小数
     */
    public static int DECIMAL_TYPE_0_1 = 3;
    /**
     * 固定1位小数
     */
    public static int DECIMAL_TYPE_1_1 = 4;

    /**
     * 保留两位小数
     *
     * @param number
     * @return
     */
    public static String formatTwoDecimals(String number) {
        return formatTwoDecimals(parseDouble(number));
    }

    /**
     * 保留两位小数
     *
     * @param number
     * @return
     */
    public static String formatTwoDecimals(double number) {
        return formatTwoDecimals(number, DECIMAL_TYPE_2_2);
    }

    /**
     * 按指定方式保留小数位數
     *
     * @param number
     * @return
     */
    public static String formatTwoDecimals(String number, int curDecimalType) {
        return formatTwoDecimals(parseDouble(number), curDecimalType);
    }

    private static double parseDouble(String number) {
        double result = 0.0;
        try {
            result = Double.parseDouble(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按指定方式保留小数位數
     *
     * @param number
     * @return
     */
    public static String formatTwoDecimals(double number, int curDecimalType) {
        if (curDecimalType == DECIMAL_TYPE_0_2) {
            return formatZero2TwoDecimals(number);
        } else if (curDecimalType == DECIMAL_TYPE_1_2) {
            return formatOne2TwoDecimals(number);
        } else if (curDecimalType == DECIMAL_TYPE_2_2) {
            return formatTwo2TwoDecimals(number);
        } else {
            return number + "";
        }
    }

    /**
     * 最多保留2位小数，最少保留0位小数
     *
     * @param number
     * @return
     */
    private static String formatZero2TwoDecimals(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }

    /**
     * 最多保留2位小数，最少保留1位小数
     *
     * @param number
     * @return
     */
    private static String formatOne2TwoDecimals(double number) {
        return String.format(number + "", "%.2f");
    }

    /**
     * 固定保留2位小数
     *
     * @param number
     * @return
     */
    private static String formatTwo2TwoDecimals(double number) {
        BigDecimal bd = new BigDecimal(number);
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 保留1位小数
     *
     * @param number
     * @return
     */
    public static String formatOneDecimals(String number) {
        return formatOneDecimals(parseDouble(number));
    }

    /**
     * 保留1位小数
     *
     * @param number
     * @return
     */
    public static String formatOneDecimals(double number) {
        return formatOneDecimals(number, DECIMAL_TYPE_1_1);
    }

    /**
     * 按指定方式保留小数位數
     *
     * @param number
     * @return
     */
    public static String formatOneDecimals(String number, int curDecimalType) {
        return formatOneDecimals(parseDouble(number), curDecimalType);
    }

    /**
     * 按指定方式保留小数位數
     *
     * @param number
     * @return
     */
    public static String formatOneDecimals(double number, int curDecimalType) {
        if (curDecimalType == DECIMAL_TYPE_0_1) {
            return formatZero2OneDecimals(number);
        } else if (curDecimalType == DECIMAL_TYPE_1_1) {
            return formatOne2OneDecimals(number);
        } else {
            return number + "";
        }
    }

    /**
     * 最多保留1位小数，最少保留0位小数
     *
     * @param number
     * @return
     */
    private static String formatZero2OneDecimals(double number) {
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(number);
    }

    /**
     * 固定保留1位小数
     *
     * @param number
     * @return
     */
    private static String formatOne2OneDecimals(double number) {
        BigDecimal bd = new BigDecimal(number);
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 四舍五入取整
     *
     * @param number
     * @return
     */
    public static String format2Int(double number) {
        BigDecimal bd = new BigDecimal(number);
        return bd.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

}
