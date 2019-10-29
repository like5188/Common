package com.like.common.sample.dialog

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityDialogBinding

/**
 * 对话框测试
 */
class DialogActivity : AppCompatActivity() {
    private val mBinding: ActivityDialogBinding by lazy {
        DataBindingUtil.setContentView<ActivityDialogBinding>(this, R.layout.activity_dialog)
    }
    private val mDialogFragment1 = DialogFragment1()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBinding
    }

    fun showDialogFragment1(view: View) {
        mDialogFragment1.setTitle("title")
        mDialogFragment1.setMessage("message")
        mDialogFragment1.show(this)
    }

}
