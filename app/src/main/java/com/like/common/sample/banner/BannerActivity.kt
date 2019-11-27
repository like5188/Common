package com.like.common.sample.banner

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityBannerBinding
import com.like.common.util.ImageUtils
import com.like.common.util.StatusBarUtils
import com.like.common.util.onPreDrawListener
import com.like.common.view.banner.BannerController
import com.like.common.view.banner.indicator.BannerIndicator
import com.like.common.view.banner.indicator.NumberIndicator
import com.like.common.view.viewPagerTransformer.RotateYTransformer
import kotlinx.android.synthetic.main.activity_banner.*
import java.util.*

class BannerActivity : AppCompatActivity() {
    private val mBinding: ActivityBannerBinding by lazy {
        DataBindingUtil.setContentView<ActivityBannerBinding>(this, R.layout.activity_banner)
    }
    private val mBannerController: BannerController by lazy { BannerController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding

        val bannerInfoList = ArrayList<BannerInfo>()
        val bannerInfo1 = BannerInfo()
        bannerInfo1.imageUrl = "https://mall02.sogoucdn.com/image/2019/03/18/20190318094408_4590.png"
        bannerInfoList.add(bannerInfo1)
        val bannerInfo2 = BannerInfo()
        bannerInfo2.imageUrl = "https://mall03.sogoucdn.com/image/2019/05/13/20190513191053_4977.png"
        bannerInfoList.add(bannerInfo2)
        val bannerInfo3 = BannerInfo()
        bannerInfo3.imageUrl = "https://mall03.sogoucdn.com/image/2018/12/21/20181221191646_4221.png"
        bannerInfoList.add(bannerInfo3)

        initAutoPlayBanner(bannerInfoList)
//        initBanner(bannerInfoList)
    }

    private fun initAutoPlayBanner(data: List<BannerInfo>) {
        mBinding.vp.setScrollSpeed()

        mBinding.vp.onPreDrawListener {
            it.layoutParams.height = (it.width * 0.4f).toInt()// vp 的高度是宽度的 0.4
        }

        mBinding.vp.adapter = MyBannerPagerAdapter(this, data)

        val indicator: BannerIndicator = NumberIndicator(this, data.size, indicatorContainer).apply {
            setPadding(10, 10, 10, 10)
            setTextSize(12f)
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.DKGRAY)
        }
//        val indicator: BannerIndicator = ImageIndicator(this, data.size, indicatorContainer, 10, listOf(R.drawable.store_point2), listOf(R.drawable.store_point1))
        indicator.setViewPager(mBinding.vp)

        mBannerController.setViewPager(mBinding.vp).setCycleInterval(3000L)
    }

    private fun initBanner(data: List<BannerInfo>) {
        mBinding.viewPager.offscreenPageLimit = 3
        mBinding.viewPager.setPageTransformer(true, object : RotateYTransformer() {
            override fun getRotate(context: Context): Float {
                var rotate = 0.5f
                (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.apply {
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
        mBinding.viewPager.adapter = MyViewPagerAdapter(this, data)
        StatusBarUtils.setStatusBarTranslucent(this)
        mBinding.viewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    private val argbEvaluator = ArgbEvaluator()

                    override fun onPageScrollStateChanged(p0: Int) {
                    }

                    override fun onPageScrolled(p0: Int, positionOffset: Float, p2: Int) {
                        when (p0) {
                            data.size - 1 -> {
                                val iv = mBinding.viewPager.getChildAt(p0).findViewById<ImageView>(R.id.iv)
                                if (iv != null && iv.drawable != null) {
                                    val color1 = ImageUtils.getColor(iv.drawable, 0x000000)
                                    val color2 = ImageUtils.getColor(iv.drawable, 0x000000)
                                    // 根据 positionOffset 计算 color1 到 color2 的渐变颜色值。使得 root 的背景色随着滑动渐变。
                                    mBinding.root.setBackgroundColor(argbEvaluator.evaluate(positionOffset, color1, color2).toString().toInt())
                                }
                            }
                            else -> {
                                val iv0 = mBinding.viewPager.getChildAt(p0).findViewById<ImageView>(R.id.iv)
                                val iv1 = mBinding.viewPager.getChildAt(p0 + 1).findViewById<ImageView>(R.id.iv)
                                if (iv0 != null && iv0.drawable != null && iv1 != null && iv1.drawable != null) {
                                    val color1 = ImageUtils.getColor(iv0.drawable, 0x000000)
                                    val color2 = ImageUtils.getColor(iv1.drawable, 0x000000)
                                    mBinding.root.setBackgroundColor(argbEvaluator.evaluate(positionOffset, color1, color2).toString().toInt())
                                }
                            }
                        }
                    }

                    override fun onPageSelected(p0: Int) {
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        mBannerController?.play()
    }

    override fun onPause() {
        super.onPause()
        mBannerController?.pause()
    }

}
