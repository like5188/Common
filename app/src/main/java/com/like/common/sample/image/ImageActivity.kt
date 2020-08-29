package com.like.common.sample.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityImageBinding
import com.like.common.util.ImageUtils
import com.like.common.util.StorageUtils
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
        File(StorageUtils.InternalStorageHelper.getCacheDir(this), "cache3.jpg")
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

    fun matrix1(view: View) {
        lifecycleScope.launch {
            showOriginBitmap(bitmap)
            val compressBitmap = ImageUtils.scaleByMatrix(this@ImageActivity, bitmap, 1000)
            showCompressBitmap(compressBitmap)
        }
    }

    fun matrix2(view: View) {
        lifecycleScope.launch {
            showOriginBitmap(bitmap)
            val compressBitmap = ImageUtils.scaleByMatrix(this@ImageActivity, bitmap, 480, 800)
            showCompressBitmap(compressBitmap)
        }
    }

    fun options(view: View) {
        lifecycleScope.launch {
            showOriginBitmap(bitmap)
            val compressBitmap = ImageUtils.scaleByOptions(this@ImageActivity, file.absolutePath, 480, 800)
            showCompressBitmap(compressBitmap)
        }
    }

    fun quality(view: View) {
        lifecycleScope.launch {
            showOriginBitmap(bitmap)
            val compressFile = File(StorageUtils.InternalStorageHelper.getCacheDir(this@ImageActivity), "cache2.jpg")
            ImageUtils.compressByQualityAndStore(this@ImageActivity, bitmap, 1000, compressFile)
            showCompressBitmap(BitmapFactory.decodeFile(compressFile.absolutePath))
        }
    }

    private fun showOriginBitmap(bitmap: Bitmap?) {
        if (null == bitmap || bitmap.isRecycled) return
        mBinding.ivOrigin.post {
            mBinding.ivOrigin.setImageBitmap(ImageUtils.getRoundedCornersBitmap(bitmap, 300, ImageUtils.CORNER_NONE))
        }
        mBinding.iv1.post {
            mBinding.iv1.setImageBitmap(ImageUtils.getRoundedCornersBitmap(bitmap, 300, ImageUtils.CORNER_TOP_RIGHT))
        }
        mBinding.iv2.post {
            mBinding.iv2.setImageBitmap(ImageUtils.getRoundedCornersBitmap(bitmap, 300, ImageUtils.CORNER_BOTTOM_RIGHT))
        }
    }

    private fun showCompressBitmap(bitmap: Bitmap?) {
        if (null == bitmap || bitmap.isRecycled) return
        mBinding.ivCompress.post {
            mBinding.ivCompress.setImageBitmap(ImageUtils.getReflectionBitmapWithOrigin(bitmap))
        }
    }

}
