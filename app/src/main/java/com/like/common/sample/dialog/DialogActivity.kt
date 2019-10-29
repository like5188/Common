package com.like.common.sample.dialog

import android.os.Bundle
import android.view.View
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
        mBinding
    }

    fun showDialogFragment1(view: View) {
        mDialogFragment1.setTitle("title")
        mDialogFragment1.setMessage("message")
        mDialogFragment1.show(this)
    }

}
