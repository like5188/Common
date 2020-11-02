package com.like.common.sample.storage

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityStorageBinding
import com.like.common.util.Logger
import com.like.common.util.MediaStoreUtils
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
        SAFUtils.createDocument(this, "123.jpg", "image/jpg") {
            Logger.d("createFile：$it")
        }
    }

    fun getFiles(view: View) {
        Logger.printCollection(MediaStoreUtils.getFiles(this))
    }

    fun getImages(view: View) {
        Logger.printCollection(MediaStoreUtils.getImages(this))
    }

    fun getAudios(view: View) {
        Logger.printCollection(MediaStoreUtils.getAudios(this))
    }

    fun getVideos(view: View) {
        Logger.printCollection(MediaStoreUtils.getVideos(this))
    }
}
