package com.like.common.util;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.HashMap;

/**
 * MD5编码的工具类
 */
public class MD5Utils {
    /**
     * 保存md5加密的源码，用于解码，key为md5码，value为源码
     **/
    public static HashMap<String, String> md5Map = new HashMap<String, String>();

    /**
     * 获取32位字符串
     *
     * @param string
     * @return
     */
    public static String get32MD5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        try {
            // 创建消息汇编对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 先将指定的字符串转换为一个16位的byte[]
            byte[] buffer = md.digest(string.getBytes("utf-8"));
            // 遍历取出数组中的每个byte元素
            for (byte b : buffer) {
                // 将取出的byte值与255(0xff)做与运算(&)后得到一个255以内的数值
                int number = b & 0xff;
                // 将得到的数值转换为16进制的字符串, 如果它只有一位, 在它的前面补0
                String hexString = Integer.toHexString(number);
                // 将生成的16个二位16进制形式的字符串连接起来, 它就是md5加密后的32位字符串
                if (hexString.length() == 1) {
                    sb.append("0").append(hexString);
                } else {
                    sb.append(hexString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        md5Map.put(sb.toString(), string);
        return sb.toString();
    }

    /**
     * 获取16位字符串
     *
     * @param string
     * @return
     */
    public static String get16MD5(String string) {
        String s = null;
        // 用来将字节转换成 16 进制表示的字
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest
                    .getInstance("MD5");
            md.update(string.getBytes());
            byte tmp[] = md.digest(); // MD5 的计算结果是128 位的长整数，
            // 用字节表示就16 个字
            char str[] = new char[16 * 2]; // 每个字节16 进制表示的话，使用两个字符，
            // 表示16 进制 32 个字
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第字节，对 MD5 的每字节
                // 转换16 进制字符的转
                byte byte0 = tmp[i]; // 取第 i 个字
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中4 位的数字转换,
                // >>>
                // 为辑右移，将符号位右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符

        } catch (Exception e) {

        }
        md5Map.put(s, string);
        return s;
    }

}

