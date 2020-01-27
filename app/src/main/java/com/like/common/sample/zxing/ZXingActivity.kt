package com.like.common.sample.zxing

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityZxingBinding
import com.like.common.util.ZXingUtils

class ZXingActivity : AppCompatActivity() {
    private val mBinding: ActivityZxingBinding by lazy {
        DataBindingUtil.setContentView<ActivityZxingBinding>(this, R.layout.activity_zxing)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.sv.setViewFinder(ZXingUtils.DefaultViewFinder(this, heightWidthRatio = 1f))
        mBinding.sv.setEnableZXing(true)
//        mBinding.sv.setEnableZBar(true)
//        mBinding.sv.setEnableIdCard(true)
        mBinding.sv.setCallback { result ->
            mBinding.tvScanResult.text = result.toString()
            mBinding.sv.restartPreviewAfterDelay(2000)
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.sv.onResume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.sv.onPause()
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

}
