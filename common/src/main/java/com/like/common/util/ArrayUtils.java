package com.like.common.util;

import java.util.Arrays;

/**
 * 数组相关的工具类
 */
public class ArrayUtils {

    /**
     * 合并数组
     *
     * @param appSecrets
     * @param b
     * @return
     */
    public static byte[] concatArray(byte[] appSecrets, byte[]... b) {
        int totalLength = appSecrets.length;
        for (byte[] array : b) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(appSecrets, totalLength);
        int offset = appSecrets.length;
        for (byte[] array : b) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * 组合两个数组为一个
     *
     * @param array1
     * @param array2
     * @return
     */
    public static String[] composeArray(String[] array1, String[] array2) {
        if (array1 == null && array2 == null) {
            return null;
        } else if (array1 == null) {
            return array2;
        } else if (array2 == null) {
            return array1;
        } else {
            String[] results = new String[array1.length + array2.length];
            results = Arrays.copyOf(array2, results.length);
            System.arraycopy(array1, 0, results, array2.length, array1.length);
            return results;
        }
    }

    /**
     * 组合两个数组为一个
     *
     * @param array1
     * @param array2
     * @return
     */
    public static Double[] composeArray(Double[] array1, Double[] array2) {
        if (array1 == null && array2 == null) {
            return null;
        } else if (array1 == null) {
            return array2;
        } else if (array2 == null) {
            return array1;
        } else {
            Double[] results = new Double[array1.length + array2.length];
            results = Arrays.copyOf(array2, results.length);
            System.arraycopy(array1, 0, results, array2.length, array1.length);
            return results;
        }
    }
}
