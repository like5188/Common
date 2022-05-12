package com.like.common.sample.zxing

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityZxingBinding
import com.like.common.util.Logger
import com.like.common.util.ZXingUtils
import com.like.common.util.activityresultlauncher.requestPermission

class ZXingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityZxingBinding>(this, R.layout.activity_zxing)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission(Manifest.permission.CAMERA) {
            Logger.e("requestPermission Manifest.permission.CAMERA:$it")
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA, it)
            if (it) {
                mBinding.sv.setViewFinder(ZXingUtils.DefaultViewFinder(this@ZXingActivity, heightWidthRatio = 1f))
//            mBinding.sv.setEnableZXing(true)
//            mBinding.sv.setEnableZBar(true)
                mBinding.sv.setEnableIdCard(true)
                mBinding.sv.setCallback { result ->
                    mBinding.tvScanResult.text = result.toString()
                    mBinding.sv.restartPreviewAfterDelay(2000)
                }
            }
        }
    }

    private fun shouldShowRequestPermissionRationale(permission: String, granted: Boolean): Boolean {
        /*
         * 我们可以通过该方法判断是否要继续申请权限
         * 1、api >= 30，没有"不再提示"选择框让用户勾选，系统会在拒绝两次后直接不再提示。
         * 2、用户可以在权限被拒绝后使用此方法来判断是否属于"不再提示"。
         * ①返回 true 表示用户没有勾选"不再提示"。
         * ②返回 false 表示用户勾选了"不再提示"。
         */
        val result = !granted && !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        if (result) {
            // 用户选择 "不再询问" 后的提示方案
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("授权失败")
                .setMessage("您需要授权此权限才能使用此功能")
                .setPositiveButton("去授权") { dialog, which -> // 跳转到设置界面
                    val intent = Intent()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                    intent.data = Uri.fromParts("package", packageName, null)
                    startActivity(intent)
                }
                .setNegativeButton("取消") { dialog, which -> }
                .create()
                .show()
        }
        return result
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
