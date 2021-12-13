package com.like.common.sample.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import coil.load
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityImageBinding
import com.like.common.util.ImageUtils
import com.like.common.util.storage.internal.InternalStorageUtils
import kotlinx.coroutines.launch
import java.io.File

/**
 * 图片压缩测试
 */
class ImageActivity : AppCompatActivity() {
    private val mBinding: ActivityImageBinding by lazy {
        DataBindingUtil.setContentView<ActivityImageBinding>(this, R.layout.activity_image)
    }
    private val file: File by lazy {
        File(InternalStorageUtils.getCacheDir(this), "cache3.jpg")
    }
    private val bitmap: Bitmap by lazy {
        BitmapFactory.decodeFile(file.absolutePath)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        lifecycleScope.launch {
            if (!file.exists()) {
                file.createNewFile()
            }
            file.writeBytes(assets.open("a.jpg").readBytes())
        }
    }

    fun compressByMatrix1(view: View) {
        lifecycleScope.launch {
            mBinding.ivOrigin.load(bitmap)
            val compressBitmap = ImageUtils.compressByMatrix(this@ImageActivity, bitmap, 1000)
            mBinding.ivCompress.load(compressBitmap)
        }
    }

    fun compressByMatrix2(view: View) {
        lifecycleScope.launch {
            mBinding.ivOrigin.load(bitmap)
            val compressBitmap = ImageUtils.compressByMatrix(this@ImageActivity, bitmap, 480, 800)
            mBinding.ivCompress.load(compressBitmap)
        }
    }

    fun compressByInSampleSize(view: View) {
        lifecycleScope.launch {
            mBinding.ivOrigin.load(bitmap)
            val compressBitmap = ImageUtils.compressByInSampleSize(this@ImageActivity, file.absolutePath, 480, 800)
            mBinding.ivCompress.load(compressBitmap)
        }
    }

    fun compressByQuality(view: View) {
        lifecycleScope.launch {
            mBinding.ivOrigin.load(bitmap)
            ImageUtils.compressByQuality(this@ImageActivity, bitmap, 1000)?.apply {
                mBinding.ivCompress.load(BitmapFactory.decodeFile(absolutePath))
            }
        }
    }

}
