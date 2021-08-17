package com.like.common.sample.anim

import android.animation.*
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityAnimBinding
import com.like.common.util.dp

/**
 * Activity 相关的测试
 */
class AnimActivity : AppCompatActivity() {
    companion object {
        private val TAG = AnimActivity::class.java.simpleName
    }

    private val mBinding: ActivityAnimBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_anim)
    }
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.btn.setOnClickListener {
//            // 关键帧动画
//            val kf0 = Keyframe.ofFloat(0f, 0f)
//            val kf1 = Keyframe.ofFloat(.5f, 360f)
//            val kf2 = Keyframe.ofFloat(1f, 0f)
//            val pvhRotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1, kf2)
//            ObjectAnimator.ofPropertyValuesHolder(mBinding.btn, pvhRotation).apply {
//                duration = 5000
//                start()
//            }
//
//            // 多个属性动画：三种方式
//            // 1、一个 ObjectAnimator
//            val pvhX = PropertyValuesHolder.ofFloat("x", 50f)
//            val pvhY = PropertyValuesHolder.ofFloat("y", 100f)
//            ObjectAnimator.ofPropertyValuesHolder(mBinding.btn, pvhX, pvhY).start()
//            // 2、多个 ObjectAnimator
//            val animX = ObjectAnimator.ofFloat(mBinding.btn, "x", 0f)
//            val animY = ObjectAnimator.ofFloat(mBinding.btn, "y", 0f)
//            AnimatorSet().apply {
//                playTogether(animX, animY)
//                start()
//            }
//            // 3、ViewPropertyAnimator
//            mBinding.btn.animate().translationX(100f).translationY(200f)
//
//            mBinding.llContainer.addView(TextView(this).apply {
//                text = "${i++}"
//                setBackgroundColor(Color.GRAY)
//                gravity = Gravity.CENTER
//                layoutParams =
//                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
//                        topMargin = 10.dp
//                    }
//                setPadding(10.dp, 10.dp, 10.dp, 10.dp)
//                setOnClickListener {
//                    mBinding.llContainer.removeView(this)
//                }
//            })

            // 使用xml中的属性动画
            (AnimatorInflater.loadAnimator(this, R.animator.animator) as AnimatorSet).apply {
                setTarget( mBinding.btn)
                start()
            }
        }
    }

}
