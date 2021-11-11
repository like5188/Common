package com.like.common.util

import android.app.Activity
import androidx.fragment.app.Fragment
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
PictureSelector.create(this)
   .openGallery()//相册 媒体类型 PictureMimeType.ofAll()、ofImage()、ofVideo()、ofAudio()
 //.openCamera()//单独使用相机 媒体类型 PictureMimeType.ofImage()、ofVideo()
   .theme()// xml样式配制 R.style.picture_default_style、picture_WeChat_style or 更多参考Demo
   .loadImageEngine()// 图片加载引擎 需要 implements ImageEngine接口
   .selectionMode()//单选or多选 PictureConfig.SINGLE PictureConfig.MULTIPLE
   .isPageStrategy()//开启分页模式，默认开启另提供两个参数；pageSize每页总数；isFilterInvalidFile是否过滤损坏图片
   .isSingleDirectReturn()//PictureConfig.SINGLE模式下是否直接返回
   .isWeChatStyle()//开启R.style.picture_WeChat_style样式
   .setPictureStyle()//动态自定义相册主题
   .setPictureCropStyle()//动态自定义裁剪主题
   .setPictureWindowAnimationStyle()//相册启动退出动画
   .isCamera()//列表是否显示拍照按钮
   .isZoomAnim()//图片选择缩放效果
   .imageFormat()//拍照图片格式后缀,默认jpeg, PictureMimeType.PNG，Android Q使用PictureMimeType.PNG_Q
   .maxSelectNum()//最大选择数量,默认9张
   .minSelectNum()// 最小选择数量
   .maxVideoSelectNum()//视频最大选择数量
   .minVideoSelectNum()//视频最小选择数量
   .videoMaxSecond()// 查询多少秒以内的视频
   .videoMinSecond()// 查询多少秒以内的视频
   .imageSpanCount()//列表每行显示个数
   .openClickSound()//是否开启点击声音
   .selectionMedia()//是否传入已选图片
   .recordVideoSecond()//录制视频秒数 默认60s
   .previewEggs()//预览图片时是否增强左右滑动图片体验
   .cropCompressQuality()// 注：已废弃 改用cutOutQuality()
   .isGif()//是否显示gif
   .previewImage()//是否预览图片
   .previewVideo()//是否预览视频
   .enablePreviewAudio()//是否预览音频
   .enableCrop()//是否开启裁剪
   .cropWH()// 裁剪宽高比,已废弃，改用. cropImageWideHigh()方法
   .cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
   .withAspectRatio()//裁剪比例
   .cutOutQuality()// 裁剪输出质量 默认100
   .freeStyleCropEnabled()//裁剪框是否可拖拽
   .circleDimmedLayer()// 是否开启圆形裁剪
   .setCircleDimmedColor()//设置圆形裁剪背景色值
   .setCircleDimmedBorderColor()//设置圆形裁剪边框色值
   .setCircleStrokeWidth()//设置圆形裁剪边框粗细
   .showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
   .showCropGrid()//是否显示裁剪矩形网格 圆形裁剪时建议设为false
   .rotateEnabled()//裁剪是否可旋转图片
   .scaleEnabled()//裁剪是否可放大缩小图片
   .isDragFrame()//是否可拖动裁剪框(固定)
   .hideBottomControls()//显示底部uCrop工具栏
   .basicUCropConfig()//对外提供ucrop所有的配制项
   .compress()//是否压缩
   .compressFocusAlpha()//压缩后是否保持图片的透明通道
   .minimumCompressSize()// 小于多少kb的图片不压缩
   .videoQuality()//视频录制质量 0 or 1
   .compressQuality()//图片压缩后输出质量
   .synOrAsy()//开启同步or异步压缩
   .queryMaxFileSize()//查询指定大小内的图片、视频、音频大小，单位M
   .compressSavePath()//自定义压缩图片保存地址，注意Q版本下的适配
   .sizeMultiplier()//glide加载大小，已废弃
   .glideOverride()//glide加载宽高，已废弃
   .isMultipleSkipCrop()//多图裁剪是否支持跳过
   .isMultipleRecyclerAnimation()// 多图裁剪底部列表显示动画效果
   .querySpecifiedFormatSuffix()//只查询指定后缀的资源，PictureMimeType.ofJPEG() ...
   .isReturnEmpty()//未选择数据时按确定是否可以退出
   .isAndroidQTransform()//Android Q版本下是否需要拷贝文件至应用沙盒内
   .setRequestedOrientation()//屏幕旋转方向 ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ...
   .isOriginalImageControl()//开启原图选项
   .bindCustomPlayVideoCallback()//自定义视频播放拦截
   .bindCustomCameraInterfaceListener()//自定义拍照回调接口
   .bindCustomPreviewCallback()// 自定义图片预览回调接口
   .cameraFileName()//自定义拍照文件名，如果是相册内拍照则内部会自动拼上当前时间戳防止重复
   .renameCompressFile()//自定义压缩文件名，多张压缩情况下内部会自动拼上当前时间戳防止重复
   .renameCropFileName()//自定义裁剪文件名，多张裁剪情况下内部会自动拼上当前时间戳防止重复
   .setRecyclerAnimationMode()//列表动画效果,AnimationType.ALPHA_IN_ANIMATION、SLIDE_IN_BOTTOM_ANIMATION
   .isUseCustomCamera()// 开启自定义相机
   .setButtonFeatures()// 自定义相机按钮状态,CustomCameraView.BUTTON_STATE_BOTH
   .setLanguage()//国际化语言 LanguageConfig.CHINESE、ENGLISH、JAPAN等
   .isWithVideoImage()//图片和视频是否可以同选,只在ofAll模式下有效
   .isMaxSelectEnabledMask()//选择条件达到阀时列表是否启用蒙层效果
   .isAutomaticTitleRecyclerTop()//图片列表超过一屏连续点击顶部标题栏快速回滚至顶部
   .loadCacheResourcesCallback()//获取ImageEngine缓存图片，参考Demo
   .setOutputCameraPath()// 自定义相机输出目录只针对Android Q以下版本，具体参考Demo
   .forResult();//结果回调分两种方式onActivityResult()和OnResultCallbackListener方式
 */

suspend fun Activity.selectSinglePhoto(): Pair<String, String>? = PictureSelector.create(this).selectSinglePhoto()

suspend fun Fragment.selectSinglePhoto(): Pair<String, String>? = PictureSelector.create(this).selectSinglePhoto()

suspend fun Activity.selectMultiplePhoto(
    selectionData: List<LocalMedia>? = null,
    maxSelectNum: Int = Int.MAX_VALUE
): List<Pair<String, String>>? = PictureSelector.create(this).selectMultiplePhoto(selectionData, maxSelectNum)

suspend fun Fragment.selectMultiplePhoto(
    selectionData: List<LocalMedia>? = null,
    maxSelectNum: Int = Int.MAX_VALUE
): List<Pair<String, String>>? = PictureSelector.create(this).selectMultiplePhoto(selectionData, maxSelectNum)

/**
 * 选择多张照片
 * 对com.github.LuckSiege.PictureSelector库进行了封装
 * @param selectionData     已经选择的照片数据
 * @param maxSelectNum      允许最多选择的照片数量
 *
 * @return 成功返回照片数据集合 List<Pair<String, String>>。集合中的第一个参数是压缩图地址、第二个参数为上传图地址。如果没有选择就返回 null
 */
private suspend fun PictureSelector.selectMultiplePhoto(
    selectionData: List<LocalMedia>?,
    maxSelectNum: Int
): List<Pair<String, String>>? = withContext(Dispatchers.IO) {
    suspendCoroutine { continuation ->
        this@selectMultiplePhoto.openGallery(PictureMimeType.ofImage())
            .imageEngine(CoilEngine.instance)
            .selectionMode(PictureConfig.MULTIPLE)
            .isCompress(true)//是否压缩
            .compressQuality(80)
            .minimumCompressSize(1024)// 小于多少kb的图片不压缩
            .maxSelectNum(maxSelectNum)
            .selectionData(selectionData)
            .imageSpanCount(3)// 每行显示个数 int
            .isPreviewImage(true)// 是否可预览图片 true or false
            .isCamera(true)// 是否显示拍照按钮 true or false
            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
            .isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: MutableList<LocalMedia>?) {
                    result.print()//打印结果
                    if (result.isNullOrEmpty()) {
                        continuation.resume(null)
                        return
                    }
                    continuation.resume(result.map {
                        //压缩文件路径
                        val compressPath = it.compressPath
                        //上传文件路径
                        val uploadPath = it.getUploadPath()
                        Pair(compressPath, uploadPath)
                    })
                }

                override fun onCancel() {
                    continuation.resume(null)
                }

            })
    }
}

/**
 * 选择1张照片
 * 对com.github.LuckSiege.PictureSelector库进行了封装
 *
 * @return 成功返回Pair<String, String>类型数据。其中第一个参数是压缩图地址、第二个参数为上传图地址。如果没有选择就返回 null
 */
private suspend fun PictureSelector.selectSinglePhoto(): Pair<String, String>? = withContext(Dispatchers.IO) {
    suspendCoroutine { continuation ->
        this@selectSinglePhoto.openGallery(PictureMimeType.ofImage())
            .imageEngine(CoilEngine.instance)
            .selectionMode(PictureConfig.SINGLE)
            .isCompress(true)//是否压缩
            .compressQuality(80)
            .minimumCompressSize(1024)// 小于多少kb的图片不压缩
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: MutableList<LocalMedia>?) {
                    result.print()//打印结果
                    if (result.isNullOrEmpty()) {
                        continuation.resume(null)
                        return
                    }
                    val media: LocalMedia = result[0]
                    //压缩文件路径
                    val compressPath = media.compressPath
                    //上传文件路径
                    val uploadPath = media.getUploadPath()
                    continuation.resume(Pair(compressPath, uploadPath))
                }

                override fun onCancel() {
                    continuation.resume(null)
                }

            })
    }
}

/**
 * 获取上传文件路径
 */
fun LocalMedia.getUploadPath() = when {
    !androidQToPath.isNullOrEmpty() -> androidQToPath
    !path.isNullOrEmpty() -> path
    else -> compressPath
}

/**
 * 打印 LocalMedia 列表
 */
fun List<LocalMedia>?.print() {
    if (this.isNullOrEmpty()) return
    // 例如 LocalMedia 里面返回五种path
    // 1.media.getPath(); 为原图path
    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
    // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
    // 5.media.getAndroidQToPath();为Android Q版本特有返回的字段，此字段有值就用来做上传使用
    // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩
    for (media in this) {
        Logger.i("是否压缩:" + media.isCompressed)
        Logger.i("压缩:" + media.compressPath)
        Logger.i("原图:" + media.path)
        Logger.i("是否裁剪:" + media.isCut)
        Logger.i("裁剪:" + media.cutPath)
        Logger.i("是否开启原图:" + media.isOriginal)
        Logger.i("原图路径:" + media.originalPath)
        Logger.i("Android Q 特有Path:" + media.androidQToPath)
    }
}