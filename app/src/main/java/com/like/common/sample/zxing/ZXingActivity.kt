package com.like.common.sample.zxing

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityZxingBinding
import com.like.common.util.ZXingUtils
import com.like.common.util.longToastCenter
import com.yzq.zxinglibrary.bean.ZxingConfig


class ZXingActivity : AppCompatActivity() {
    private val mBinding: ActivityZxingBinding by lazy {
        DataBindingUtil.setContentView<ActivityZxingBinding>(this, R.layout.activity_zxing)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun createBarCode(view: View) {
        val bmp = ZXingUtils.createBarCode("1234567890", 800, 200)
        mBinding.iv.setImageBitmap(bmp)
    }

    fun createQRCode1(view: View) {
        val bmp = ZXingUtils.createQRCode("https://www.baidu.com/", 400, 400, null)
        mBinding.iv.setImageBitmap(bmp)
    }

    fun createQRCode2(view: View) {
        val bmp = ZXingUtils.createQRCode("https://www.baidu.com/", 400, 400, BitmapFactory.decodeResource(resources, R.drawable.icon_0))
        mBinding.iv.setImageBitmap(bmp)
    }

    fun scan(view: View) {
        /**
         * ZxingConfig是配置类
         * 可以设置是否显示底部布局，闪光灯，相册，
         * 是否播放提示音  震动
         * 设置扫描框颜色等
         * 也可以不传这个参数
         */
        val config = ZxingConfig()
        config.isPlayBeep = true //是否播放扫描声音 默认为true
        config.isShake = true //是否震动  默认为true
        config.isDecodeBarCode = true //是否扫描条形码 默认为true
        config.reactColor = R.color.colorAccent //设置扫描框四个角的颜色 默认为白色
        config.frameLineColor = R.color.colorAccent //设置扫描框边框颜色 默认无色
        config.scanLineColor = R.color.colorAccent //设置扫描线的颜色 默认白色
        config.isFullScreenScan = false //是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        ZXingUtils.scan(this, config, {
            longToastCenter(it)
        })
    }

}
