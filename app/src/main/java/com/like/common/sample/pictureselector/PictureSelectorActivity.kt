package com.like.common.sample.pictureselector

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityPictureSelectorBinding
import com.like.common.util.CoilEngine
import com.like.livedatarecyclerview.layoutmanager.WrapGridLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.PictureFileUtils

class PictureSelectorActivity : AppCompatActivity() {
    companion object {
        const val TAG = "PictureSelectorActivity"
    }

    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPictureSelectorBinding>(this, R.layout.activity_picture_selector)
    }
    private val mAddImageViewAdapter by lazy {
        MyAddImageViewAdapter(this, mBinding.rv, R.drawable.icon_take_photo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapGridLayoutManager(this, 4)
        mBinding.rv.adapter = mAddImageViewAdapter
    }

    fun takePhoto(view: View) {
        // 快捷调用，单独启动拍照或视频 根据PictureMimeType自动识别
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .imageEngine(CoilEngine.create())
                .isCompress(true)// 是否压缩
                .compressQuality(60)// 图片压缩后输出质量
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        if (result.isNullOrEmpty()) return
                        // 例如 LocalMedia 里面返回五种path
                        // 1.media.getPath(); 为原图path
                        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                        // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
                        // 5.media.getAndroidQToPath();为Android Q版本特有返回的字段，此字段有值就用来做上传使用
                        // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩
                        for (media in result) {
                            Log.i(TAG, "是否压缩:" + media.isCompressed)
                            Log.i(TAG, "压缩:" + media.compressPath)
                            Log.i(TAG, "原图:" + media.path)
                            Log.i(TAG, "是否裁剪:" + media.isCut)
                            Log.i(TAG, "裁剪:" + media.cutPath)
                            Log.i(TAG, "是否开启原图:" + media.isOriginal)
                            Log.i(TAG, "原图路径:" + media.originalPath)
                            Log.i(TAG, "Android Q 特有Path:" + media.androidQToPath)
                        }
                        mAddImageViewAdapter.add(result)
                    }

                    override fun onCancel() {
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                //包括裁剪和压缩后的缓存，要在上传成功后调用，type 指的是图片or视频缓存取决于你设置的ofImage或ofVideo 注意：需要系统sd卡权限
                PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage())
                // 清除所有缓存 例如：压缩、裁剪、视频、音频所生成的临时文件
//            PictureFileUtils.deleteAllCacheDirFile(this)
            }
        }.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

}
