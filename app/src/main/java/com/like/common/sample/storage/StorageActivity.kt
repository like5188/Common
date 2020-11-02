package com.like.common.sample.storage

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityStorageBinding
import com.like.common.util.Logger
import com.like.common.util.MediaStoreUtils
import com.like.common.util.MimeType
import com.like.common.util.SAFUtils
import kotlinx.coroutines.launch

class StorageActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityStorageBinding>(this, R.layout.activity_storage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun openDocument(view: View) {
        lifecycleScope.launch {
            Logger.d("openDocument：${SAFUtils.openDocument(this@StorageActivity)}")
        }
    }

    fun openDocumentTree(view: View) {
        lifecycleScope.launch {
            val documentFile = SAFUtils.openDocumentTree(this@StorageActivity)
            documentFile?.listFiles()?.forEach {
                Logger.d("openDocumentTree：${it?.uri}")
            }
        }
    }

    fun createDocument(view: View) {
        lifecycleScope.launch {
            Logger.d("createDocument：${SAFUtils.createDocument(this@StorageActivity, "123.jpg", MimeType._jpg)}")
        }
    }

    fun deleteDocument(view: View) {
        lifecycleScope.launch {
            Logger.d("deleteDocument：${SAFUtils.deleteDocument(this@StorageActivity, Uri.parse("content://com.android.providers.downloads.documents/document/1"))}")
        }
    }

    fun getFiles(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(MediaStoreUtils.getFiles(this@StorageActivity))
        }
    }

    fun getImages(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(MediaStoreUtils.getImages(this@StorageActivity))
        }
    }

    fun getAudios(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(MediaStoreUtils.getAudios(this@StorageActivity))
        }
    }

    fun getVideos(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(MediaStoreUtils.getVideos(this@StorageActivity))
        }
    }
}
