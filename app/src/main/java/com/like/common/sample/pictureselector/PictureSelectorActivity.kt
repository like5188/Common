package com.like.common.sample.pictureselector

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityPictureSelectorBinding
import com.like.common.util.PermissionUtils
import com.like.livedatarecyclerview.layoutmanager.WrapGridLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.tools.PictureFileUtils

class PictureSelectorActivity : AppCompatActivity() {
    private val mBinding: ActivityPictureSelectorBinding by lazy {
        DataBindingUtil.setContentView<ActivityPictureSelectorBinding>(this, R.layout.activity_picture_selector)
    }
    private val mAddImageViewAdapter: MyAddImageViewAdapter by lazy {
        MyAddImageViewAdapter(this, mBinding.rv, R.drawable.icon_take_photo)
    }

    private val mPermissionUtils: PermissionUtils by lazy {
        PermissionUtils().apply {
            init(this@PictureSelectorActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapGridLayoutManager(this, 4)
        mBinding.rv.adapter = mAddImageViewAdapter
    }

    fun takePhoto(view: View) {
        // 单独启动拍照或视频
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    Log.d("PictureSelectorActivity", selectList.toString())
                    mAddImageViewAdapter.add(selectList)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPermissionUtils.checkPermissions(
                {
                    //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
                    PictureFileUtils.deleteCacheDirFile(this)
                }, {}, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

}
