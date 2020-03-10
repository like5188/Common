package com.like.common.sample.cachevideoview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityCacheVideoViewBinding

class CacheVideoViewActivity : AppCompatActivity() {
    private val mBinding: ActivityCacheVideoViewBinding by lazy {
        DataBindingUtil.setContentView<ActivityCacheVideoViewBinding>(this, R.layout.activity_cache_video_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.cacheVideoView.play(
                "https://flv2.bn.netease.com/videolib1/1811/26/OqJAZ893T/HD/OqJAZ893T-mobile.mp4",
                "https://mall03.sogoucdn.com/image/2019/06/11/20190611142939_6410.jpg"
        )
    }

    override fun onDestroy() {
        mBinding.cacheVideoView.stop()
        super.onDestroy()
    }

}
