package com.like.common.sample.activitytest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTest1Binding
import com.like.common.util.AutoWired
import com.like.common.util.StartActivityForResultWrapper
import com.like.common.util.injectForIntentExtras

/**
 * 正常的 Activity
 */
class TestActivity1 : AppCompatActivity() {
    @AutoWired
    var name: String? = null

    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTest1Binding>(this, R.layout.activity_test1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectForIntentExtras()
        mBinding
    }

    fun click0(view: View) {
        setResult(Activity.RESULT_OK, Intent().apply { putExtra("name", name) })
        finish()
    }

    companion object {
        fun start(startActivityForResultWrapper: StartActivityForResultWrapper, name: String?, callback: (Intent?) -> Unit) {
            startActivityForResultWrapper.startActivityForResult<TestActivity1>("name" to name, callback = callback)
        }
    }
}
