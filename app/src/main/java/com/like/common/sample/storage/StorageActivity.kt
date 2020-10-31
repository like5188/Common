package com.like.common.sample.storage

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityStorageBinding
import com.like.common.util.Logger
import com.like.common.util.SAFUtils

class StorageActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityStorageBinding>(this, R.layout.activity_storage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun openDocument(view: View) {
        SAFUtils.openDocument(this) {
            Logger.d("openDocument：$it")
        }
    }

    fun createFile(view: View) {
        SAFUtils.createFile(this, "123.jpg") {
            Logger.d("createFile：$it")
        }
    }

    fun getAll(view: View) {
    }

    fun contains(view: View) {
    }

    fun clear(view: View) {
    }

    fun remove(view: View) {
    }
}
