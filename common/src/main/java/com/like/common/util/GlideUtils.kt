package com.like.common.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.widget.ImageView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Glide工具类
 *
 * 1、引入largeHeap属性，让系统为App分配更多的独立内存。
 * 2、禁止Glide内存缓存。设置skipMemoryCache(true)。
 * 3、自定义GlideModule。设置MemoryCache和BitmapPool大小。
 * 4、升级到Glide4.0，使用asDrawable代替asBitmap，drawable更省内存。
 * 5、ImageView的scaleType为fitXY时，改为fitCenter/centerCrop/fitStart/fitEnd显示。
 * 6、不使用application作为context。当context为application时，会把imageView是生命周期延长到整个运行过程中，imageView不能被回收，从而造成OOM异常。
 * 7、使用application作为context。但是对ImageView使用弱引用或软引用，尽量使用SoftReference，当内存不足时，将及时回收无用的ImageView。
 * 8、当列表在滑动的时候，调用Glide的pauseRequests()取消请求，滑动停止时，调用resumeRequests()恢复请求。
 * 9、Try catch某些大内存分配的操作。考虑在catch里面尝试一次降级的内存分配操作。例如decode bitmap的时候，catch到OOM，可以尝试把采样比例再增加一倍之后，再次尝试decode。
 * 10、BitmapFactory.Options和BitmapFactory.decodeStream获取原始图片的宽、高，绕过Java层加载Bitmap，再调用Glide的override(width,height)控制显示。
 * 11、图片局部加载。参考：SubsamplingScaleImageView，先将图片下载到本地，然后去加载，只加载当前可视区域，在手指拖动的时候再去加载另外的区域。
 */
class GlideUtils {
    private val glideRequests: GlideRequests
    private val glideRequest: GlideRequest<Drawable>

    constructor(context: Context) {
        glideRequests = GlideApp.with(context)
        glideRequest = glideRequests.asDrawable()
    }

    constructor(fragment: androidx.fragment.app.Fragment) {
        glideRequests = GlideApp.with(fragment)
        glideRequest = glideRequests.asDrawable()
    }

    constructor(activity: Activity) {
        glideRequests = GlideApp.with(activity)
        glideRequest = glideRequests.asDrawable()
    }

    constructor(activity: androidx.fragment.app.FragmentActivity) {
        glideRequests = GlideApp.with(activity)
        glideRequest = glideRequests.asDrawable()
    }

    /**
     * 显示图片
     */
    @JvmOverloads
    fun display(string: String, imageView: ImageView, listener: RequestListener<Drawable>? = null) {
        glideRequest.load(string).listener(listener).into(imageView)
    }

    /**
     * 显示圆形图片
     */
    @JvmOverloads
    fun displayCircle(string: String, imageView: ImageView, listener: RequestListener<Drawable>? = null) {
        glideRequest.load(string).listener(listener).circleCrop().into(imageView)
    }

    /**
     * 显示圆角矩形图片
     */
    @JvmOverloads
    fun displayRoundedCorners(string: String, imageView: ImageView, radius: Int, cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL, listener: RequestListener<Drawable>? = null) {
        glideRequest.load(string).listener(listener).transform(RoundedCornersTransformation(radius, 0, cornerType)).into(imageView)
    }

    /**
     * 显示高斯模糊图片
     */
    @JvmOverloads
    fun displayBlur(string: String, imageView: ImageView, radius: Int, listener: RequestListener<Drawable>? = null) {
        glideRequest.load(string).listener(listener).transform(BlurTransformation(radius)).into(imageView)
    }

    /**
     * 获取bitmap的尺寸
     */
    fun getBitmapSize(string: String, callback: (Bitmap, Int, Int) -> Unit) {
        glideRequests.asBitmap().load(string).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                callback(resource, resource.width, resource.height)
                glideRequests.clear(this)
            }
        })
    }

    /**
     * 指定图片是否已经缓存
     */
    @JvmOverloads
    fun hasCached(string: String, width: Int = Target.SIZE_ORIGINAL, height: Int = Target.SIZE_ORIGINAL, hasCached: (String, Boolean) -> Unit) {
        if (string.isEmpty()) hasCached(string, false)
        glideRequests.load(string)
                .onlyRetrieveFromCache(true)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        hasCached(string, false)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        hasCached(string, true)
                        return false
                    }
                })
                .submit(width, height)
    }

    /**
     * 下载图片
     *
     * @param url
     * @param onSuccess 回调，UI线程
     */
    @SuppressLint("CheckResult")
    fun downloadImage(url: String, onSuccess: (Bitmap) -> Unit, onFailure: ((Throwable) -> Unit)? = null) {
        getDownloadImageObservable(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onSuccess(it)
                }, {
                    onFailure?.invoke(it)
                })
    }

    /**
     * 批量下载图片
     *
     * @param urlList
     * @param onSuccess 回调，UI线程
     */
    @SuppressLint("CheckResult")
    fun downloadImages(urlList: List<String>, onSuccess: (Map<String, Bitmap>) -> Unit, onFailure: ((Throwable) -> Unit)? = null) {
        getDownloadImagesObservable(urlList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .collect({
                    mutableMapOf<String, Bitmap>()
                }, { map, pair ->
                    map[pair.first] = pair.second
                })
                .subscribe({
                    onSuccess(it)
                }, {
                    onFailure?.invoke(it)
                })
    }

    private fun getDownloadImagesObservable(urlList: List<String>): Observable<Pair<String, Bitmap>> =
            Observable.fromIterable(urlList)
                    .flatMap<Pair<String, Bitmap>> { s ->
                        val urlObservable = Observable.just(s)
                        val bitmapObservable = getDownloadImageObservable(s)
                        Observable.zip<String, Bitmap, Pair<String, Bitmap>>(
                                urlObservable,
                                bitmapObservable,
                                BiFunction { t1, t2 -> Pair(t1, t2) }
                        )
                    }

    private fun getDownloadImageObservable(url: String): Observable<Bitmap> =
            Observable.create<Bitmap> { observableEmitter ->
                try {
                    val futureTarget = glideRequests
                            .asBitmap()
                            .load(url)
                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    val bitmap = futureTarget.get()
                    glideRequests.clear(futureTarget)
                    observableEmitter.onNext(bitmap)
                    observableEmitter.onComplete()
                } catch (e: Exception) {
                    e.printStackTrace()
                    observableEmitter.onError(e)
                }
            }

}

/**
 * Glide V4 Generated API 需要的
 */
@GlideModule
class CustomGlideModule : AppGlideModule()