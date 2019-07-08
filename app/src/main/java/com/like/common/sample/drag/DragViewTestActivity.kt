package com.like.common.sample.drag

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.like.common.ui.BaseActivity
import com.like.common.util.GlideUtils
import com.like.common.util.ioThread
import com.like.common.view.dragview.DragViewManager
import com.like.common.R
import com.like.common.databinding.ActivityDragphotoviewBinding
import java.io.File
import java.io.IOException

class DragViewTestActivity : BaseActivity() {
    companion object {
        val TAG = DragViewTestActivity::class.java.simpleName
    }

//    private val originImageUrl0: String by lazy { "${StorageUtils.InternalStorageHelper.getCacheDir(this)}${File.separator}image_0_origin.jpg" }
//    private val originImageUrl1: String by lazy { "${StorageUtils.InternalStorageHelper.getCacheDir(this)}${File.separator}image_1_origin.jpg" }
//    private val originImageUrl2: String by lazy { "${StorageUtils.InternalStorageHelper.getCacheDir(this)}${File.separator}image_2_origin.jpg" }
//    private val imageUrl0: String by lazy { "${StorageUtils.InternalStorageHelper.getCacheDir(this)}${File.separator}image_0.jpg" }
//    private val imageUrl1: String by lazy { "${StorageUtils.InternalStorageHelper.getCacheDir(this)}${File.separator}image_1.jpg" }
//    private val imageUrl2: String by lazy { "${StorageUtils.InternalStorageHelper.getCacheDir(this)}${File.separator}image_2.jpg" }
//    private val videoUrl: String by lazy { "${StorageUtils.InternalStorageHelper.getCacheDir(this)}${File.separator}video_1.mp4" }
//    private val videoImageUrl: String by lazy { imageUrl0 }

    private val originImageUrl0: String by lazy { "http://bbsfiles.vivo.com.cn/vivobbs/attachment/forum/201601/11/183844md4d33a5d8d134w4.jpg.thumb.jpg" }
    private val originImageUrl1: String by lazy { "http://bbsfiles.vivo.com.cn/vivobbs/attachment/forum/201601/11/183837avftmmzmfi76tkis.jpg.thumb.jpg" }
    private val originImageUrl2: String by lazy { "http://bbsfiles.vivo.com.cn/vivobbs/attachment/forum/201601/11/183832tqu8fyy6qqk8a8f0.jpg.thumb.jpg" }
    private val imageUrl0: String by lazy { "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1507451649503&di=f17e048c5bca2efc2879dc8e578a73f1&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fexp%2Fw%3D500%2Fsign%3D03fdfcc6d743ad4ba62e46c0b2035a89%2F8ad4b31c8701a18b5d18132c982f07082938fee6.jpg" }
    private val imageUrl1: String by lazy { "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1507451649502&di=f6f24a7a3b395e1107b8ddceb3efe2bb&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F4034970a304e251f89ac4497a186c9177f3e5301.jpg" }
    private val imageUrl2: String by lazy { "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1507451649502&di=112f816b18998d14223ab58269f5c057&imgtype=0&src=http%3A%2F%2Fupload.gezila.com%2Fdata%2F20150906%2F28201441522774.jpg" }
    private val videoUrl: String by lazy { "http://he.yinyuetai.com/uploads/videos/common/31BA015D2B8D04657E61B4BF0B448B79.mp4" }
    private val videoImageUrl: String by lazy { "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1507451649502&di=67866dc8e157120fb44487329a18c3c9&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fexp%2Fw%3D480%2Fsign%3D788267e49c2f07085f052b08d925b865%2F9922720e0cf3d7ca849f4addf01fbe096b63a934.jpg" }

    private val mBinding: ActivityDragphotoviewBinding by lazy {
        DataBindingUtil.setContentView<ActivityDragphotoviewBinding>(this, R.layout.activity_dragphotoview)
    }

    private val glideUtils: GlideUtils by lazy { GlideUtils(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

//        copyAssets2Sd(this, "video_1.mp4", videoUrl)
//        copyAssets2Sd(this, "image_0_origin.jpg", originImageUrl0)
//        copyAssets2Sd(this, "image_1_origin.jpg", originImageUrl1)
//        copyAssets2Sd(this, "image_2_origin.jpg", originImageUrl2)
//        copyAssets2Sd(this, "image_0.jpg", imageUrl0)
//        copyAssets2Sd(this, "image_1.jpg", imageUrl1)
//        copyAssets2Sd(this, "image_2.jpg", imageUrl2)

        glideUtils.display(imageUrl0, mBinding.iv0)
        glideUtils.display(imageUrl1, mBinding.iv1)
        glideUtils.display(imageUrl2, mBinding.iv2)
        glideUtils.display(videoImageUrl, mBinding.iv3)
    }

    private fun copyAssets2Sd(context: Context, assetFileName: String, sdFilePath: String) {
        //测试把文件直接复制到sd卡中 fileSdPath完整路径
        val sdFile = File(sdFilePath)
        if (!sdFile.exists()) {
            Log.d(TAG, "************目标文件不存在,开始拷贝")
            ioThread {
                try {
                    sdFile.createNewFile()
                    sdFile.writeBytes(context.assets.open(assetFileName).readBytes())
                    Log.d(TAG, "************拷贝成功")
                } catch (e: IOException) {
                    Log.d(TAG, "************拷贝失败")
                    e.printStackTrace()
                }
            }
        } else {
            Log.d(TAG, "************目标文件已经存在")
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.iv_0, R.id.iv_1, R.id.iv_2 -> {
                DragViewManager.previewImage(
                        this,
                        listOf(
                                DragViewManager.DragInfoTemp(mBinding.iv0, thumbImageUrl = imageUrl0, url = originImageUrl0),
                                DragViewManager.DragInfoTemp(mBinding.iv1, thumbImageUrl = imageUrl1, url = originImageUrl1),
                                DragViewManager.DragInfoTemp(mBinding.iv2, thumbImageUrl = imageUrl2, url = originImageUrl2)
                        ),
                        when (view.id) {
                            R.id.iv_0 -> 0
                            R.id.iv_1 -> 1
                            R.id.iv_2 -> 2
                            else -> -1
                        }
                )
            }
            R.id.rl_video -> {
                DragViewManager.previewVideo(
                        this,
                        DragViewManager.DragInfoTemp(mBinding.iv3, thumbImageUrl = videoImageUrl, url = videoUrl)
                )
            }
        }
    }

}
