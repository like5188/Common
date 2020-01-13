package com.like.common.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.util.*

object ZxingUtils {
    /**
     * 根据条形码字符串生成条形码图片
     */
    @Throws(Exception::class)
    fun createBarCode(content: String, width: Int, height: Int): Bitmap {
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        val matrix = MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, width, height)
        //矩阵的宽度
        val matrixWidth = matrix.width
        //矩阵的高度
        val matrixHeight = matrix.height
        //矩阵像素数组
        val pixels = IntArray(matrixWidth * matrixHeight)
        //双重循环遍历每一个矩阵点
        for (y in 0 until matrixHeight) {
            (0 until matrixWidth).filter { matrix.get(it, y) }
                    .forEach {
                        //设置矩阵像素点的值
                        pixels[y * matrixWidth + it] = Color.parseColor("#ff303030")
                    }
        }
        //根据颜色数组来创建位图
        /**
         * 此函数创建位图的过程可以简单概括为为:更加width和height创建空位图，
         * 然后用指定的颜色数组colors来从左到右从上至下一次填充颜色。
         * config是一个枚举，可以用它来指定位图“质量”。
         */
        val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
        //将生成的条形码返回给调用者
        return bitmap
    }

    /**
     * 生成二维码图片（不带图片）
     */
    @Throws(WriterException::class)
    fun createQRCode(url: String, widthAndHeight: Int): Bitmap {
        val hints = Hashtable<EncodeHintType, String>()
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8")
        val matrix = MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight)

        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        //画黑点
        for (y in 0 until height) {
            (0 until width).filter { matrix.get(it, y) }
                    .forEach {
                        pixels[y * width + it] = Color.parseColor("#ff303030")
                    }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

}