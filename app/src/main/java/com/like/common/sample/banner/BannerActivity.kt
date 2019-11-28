package com.like.common.sample.banner

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
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
import com.like.common.view.banner.indicator.IBannerIndicator
import com.like.common.view.banner.indicator.StickyDotBezierCurveIndicator
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
        bannerInfoList.add(BannerInfo("https://mall02.sogoucdn.com/image/2019/03/18/20190318094408_4590.png"))
        bannerInfoList.add(BannerInfo("https://mall03.sogoucdn.com/image/2019/05/13/20190513191053_4977.png"))
        bannerInfoList.add(BannerInfo("https://mall03.sogoucdn.com/image/2018/12/21/20181221191646_4221.png"))

        initAutoPlayBanner(bannerInfoList)
//        initBanner(bannerInfoList)
    }

    private fun initAutoPlayBanner(data: List<BannerInfo>) {
        mBinding.vp.setScrollSpeed()
        mBinding.vp.adapter = MyBannerPagerAdapter(this, data)

        mBinding.vp.onPreDrawListener {
            it.layoutParams.height = (it.width * 0.4f).toInt()// vp 的高度是宽度的 0.4

//            val indicator: IBannerIndicator = TextIndicator(this, data.size, indicatorContainer).apply {
//                setTextSize(12f)
//                setTextColor(Color.WHITE)
//                setBackgroundColor(Color.DKGRAY)
//            }
//            val indicator: IBannerIndicator = ImageIndicator(this, data.size, indicatorContainer, 10f, listOf(R.drawable.store_point2), listOf(R.drawable.store_point1))
            val indicator: IBannerIndicator = StickyDotBezierCurveIndicator(this, data.size, indicatorContainer, 20f, Color.GRAY, listOf(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4")))
            indicator.setViewPager(mBinding.vp)

            mBannerController.setViewPager(mBinding.vp).setCycleInterval(3000L)
        }

    }

    private fun initBanner(data: List<BannerInfo>) {
        mBinding.viewPager.offscreenPageLimit = 2// 必须设置，因为要读取相邻图片的颜色值
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
        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            private val argbEvaluator = ArgbEvaluator()

            override fun onPageScrollStateChanged(state: Int) {
            }

            // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.e("BannerActivity", "position=$position positionOffset=$positionOffset")
                when (position) {
                    data.size - 1 -> {
                        val iv0 = mBinding.viewPager.getChildAt(position).findViewById<ImageView>(R.id.iv)
                        if (iv0 != null && iv0.drawable != null) {
                            val color1 = ImageUtils.getColor(iv0.drawable, 0x000000)
                            val color2 = ImageUtils.getColor(iv0.drawable, 0x000000)
                            // 根据 positionOffset 计算 color1 到 color2 的渐变颜色值。使得 root 的背景色随着滑动渐变。
                            mBinding.root.setBackgroundColor(argbEvaluator.evaluate(positionOffset, color1, color2).toString().toInt())
                        }
                    }
                    else -> {
                        val iv0 = mBinding.viewPager.getChildAt(position).findViewById<ImageView>(R.id.iv)
                        val iv1 = mBinding.viewPager.getChildAt(position + 1).findViewById<ImageView>(R.id.iv)
                        if (iv0 != null && iv0.drawable != null && iv1 != null && iv1.drawable != null) {
                            val color1 = ImageUtils.getColor(iv0.drawable, 0x000000)
                            val color2 = ImageUtils.getColor(iv1.drawable, 0x000000)
                            mBinding.root.setBackgroundColor(argbEvaluator.evaluate(positionOffset, color1, color2).toString().toInt())
                        }
                    }
                }
            }

            override fun onPageSelected(position: Int) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        mBannerController.play()
    }

    override fun onPause() {
        super.onPause()
        mBannerController.pause()
    }

}
