package com.like.common.util

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import androidx.fragment.app.FragmentActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.like.common.view.callback.RxCallback
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import java.util.*

object ZXingUtils {

    /**
     * 扫描条码或者二维码。
     * 已经做了权限处理
     */
    fun scan(activity: FragmentActivity, onSuccess: (String) -> Unit, onError: ((Throwable) -> Unit)? = null, config: ZxingConfig? = null) {
        val permissionUtils = PermissionUtils(activity)
        val rxCallback = RxCallback(activity)
        permissionUtils.checkCameraPermissionGroup {
            val intent = Intent(activity, CaptureActivity::class.java)
            config?.let {
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, it)
            }
            rxCallback.startActivityForResult(intent).subscribe(
                    {
                        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                            onSuccess(it.data.getStringExtra(Constant.CODED_CONTENT) ?: "")
                        }
                    },
                    {
                        onError?.invoke(it)
                    })
        }
    }

    /**
     * 根据条形码字符串生成条形码图片
     */
    fun createBarCode(content: String, w: Int, h: Int): Bitmap? {
        if (content.isEmpty()) {
            return null
        }
        if (w <= 0 || h <= 0) {
            return null
        }
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        val matrix = MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, w, h)
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
     * 生成二维码图片
     */
    fun createQRCode(content: String, w: Int, h: Int, logo: Bitmap?): Bitmap? {
        if (content.isEmpty()) {
            return null
        }
        if (w <= 0 || h <= 0) {
            return null
        }
        /*偏移量*/
        var offsetX = w / 2
        var offsetY = h / 2
        /*生成logo*/
        var logoBitmap: Bitmap? = null
        if (logo != null) {
            val matrix = Matrix()
            val scaleFactor = Math.min(w * 1.0f / 5 / logo.width, h * 1.0f / 5 / logo.height)
            matrix.postScale(scaleFactor, scaleFactor)
            logoBitmap = Bitmap.createBitmap(logo, 0, 0, logo.width, logo.height, matrix, true)
        }
        /*如果log不为null,重新计算偏移量*/
        var logoW = 0
        var logoH = 0
        if (logoBitmap != null) {
            logoW = logoBitmap.width
            logoH = logoBitmap.height
            offsetX = (w - logoW) / 2
            offsetY = (h - logoH) / 2
        }
        /*指定为UTF-8*/
        val hints = Hashtable<EncodeHintType, Any?>()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        //容错级别
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        //设置空白边距的宽度
        hints[EncodeHintType.MARGIN] = 0
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        var matrix: BitMatrix? = null
        return try {
            matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, w, h, hints)
            // 二维矩阵转为一维像素数组,也就是一直横着排了
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    if (x >= offsetX && x < offsetX + logoW && y >= offsetY && y < offsetY + logoH) {
                        var pixel = logoBitmap!!.getPixel(x - offsetX, y - offsetY)
                        if (pixel == 0) {
                            pixel = if (matrix[x, y]) {
                                -0x1000000
                            } else {
                                -0x1
                            }
                        }
                        pixels[y * w + x] = pixel
                    } else {
                        if (matrix[x, y]) {
                            pixels[y * w + x] = -0x1000000
                        } else {
                            pixels[y * w + x] = -0x1
                        }
                    }
                }
            }
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, w, 0, 0, w, h)
            }
        } catch (e: WriterException) {
            print(e)
            null
        }
    }

}