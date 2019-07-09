package com.like.common.sample.banner

import android.animation.ArgbEvaluator
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.ImageView
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityBannerBinding
import com.like.common.util.ImageUtils
import com.like.common.util.StatusBarUtils
import com.like.common.view.viewPagerTransformer.AlphaPageTransformer
import com.like.common.view.viewPagerTransformer.RotateYTransformer
import java.util.*

class BannerActivity : AppCompatActivity() {
    private val mBinding: ActivityBannerBinding by lazy {
        DataBindingUtil.setContentView<ActivityBannerBinding>(this, R.layout.activity_banner)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        initBanner()
    }

    private fun initBanner() {
        val bannerInfoList = ArrayList<BannerInfo>()
        val bannerInfo = BannerInfo()
        bannerInfo.imageUrl = "https://mall02.sogoucdn.com/image/2019/03/18/20190318094408_4590.png"
        bannerInfoList.add(bannerInfo)
        val bannerInfo1 = BannerInfo()
        bannerInfo1.imageUrl = "https://mall03.sogoucdn.com/image/2019/05/13/20190513191053_4977.png"
        bannerInfoList.add(bannerInfo1)
        val bannerInfo2 = BannerInfo()
        bannerInfo2.imageUrl = "https://mall03.sogoucdn.com/image/2018/12/21/20181221191646_4221.png"
        bannerInfoList.add(bannerInfo2)

        // 设置切换动画
        mBinding.vp.setPageTransformer(true, AlphaPageTransformer())
        mBinding.vp.setScrollSpeed()
        mBinding.bannerView
                .init(0.4f, R.drawable.store_point1, listOf(R.drawable.store_point2), R.id.vp, R.id.indicatorContainer, 10, 3000)
                .setAdapterAndPlay(BannerPagerAdapter(this, bannerInfoList))

        mBinding.viewPager.offscreenPageLimit = 3
        mBinding.viewPager.setPageTransformer(true, object : RotateYTransformer() {
            override fun getRotate(context: Context): Float {
                var rotate = 0.5f
                (context.getSystemService(Context.WINDOW_SERVICE)as? WindowManager)?.apply {
                    val metric = DisplayMetrics()
                    defaultDisplay.getMetrics(metric)
                    val densityDpi = metric.densityDpi
                    if (densityDpi <= 240) {
                        rotate = 3f
                    } else if (densityDpi <= 320) {
                        rotate = 2f
                    }
                }
                return rotate
            }
        })
        mBinding.viewPager.adapter = MyViewPagerAdapter(this, bannerInfoList)
        val argbEvaluator = ArgbEvaluator()
        StatusBarUtils.setStatusBarTranslucent(this)
        mBinding.viewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(p0: Int) {
                    }

                    override fun onPageScrolled(p0: Int, positionOffset: Float, p2: Int) {
                        when (p0) {
                            bannerInfoList.size - 1 -> {
                                val iv = mBinding.viewPager.getChildAt(p0).findViewById<ImageView>(R.id.iv)
                                if (iv != null && iv.drawable != null) {
                                    val color1 = getColor(ImageUtils.drawable2Bitmap(iv.drawable))
                                    val color2 = getColor(ImageUtils.drawable2Bitmap(iv.drawable))
                                    mBinding.root.setBackgroundColor(argbEvaluator.evaluate(positionOffset, color1, color2).toString().toInt())
                                }
                            }
                            else -> {
                                val iv0 = mBinding.viewPager.getChildAt(p0).findViewById<ImageView>(R.id.iv)
                                val iv1 = mBinding.viewPager.getChildAt(p0 + 1).findViewById<ImageView>(R.id.iv)
                                if (iv0 != null && iv0.drawable != null && iv1 != null && iv1.drawable != null) {
                                    val color1 = getColor(ImageUtils.drawable2Bitmap(iv0.drawable))
                                    val color2 = getColor(ImageUtils.drawable2Bitmap(iv1.drawable))
                                    mBinding.root.setBackgroundColor(argbEvaluator.evaluate(positionOffset, color1, color2).toString().toInt())
                                }
                            }
                        }
                    }

                    override fun onPageSelected(p0: Int) {
                    }

                })

    }

    fun getColor(bitmap: Bitmap?): Int {
        bitmap ?: R.color.colorPrimary
        val palette = Palette.from(bitmap!!).generate()
        val vibrant = palette.getVibrantColor(0x000000)
        val vibrantLight = palette.getLightVibrantColor(0x000000)
        val vibrantDark = palette.getDarkVibrantColor(0x000000)
        val muted = palette.getMutedColor(0x000000)
        val mutedLight = palette.getLightMutedColor(0x000000)
        val mutedDark = palette.getDarkMutedColor(0x000000)
        return vibrantDark
    }

}
