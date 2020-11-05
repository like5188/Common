package com.like.common.sample.storage

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityStorageBinding
import com.like.common.util.Logger
import com.like.common.util.StoragePublicUtils
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
            Logger.d("openDocument：${StoragePublicUtils.SAFHelper.openDocument(this@StorageActivity)}")
        }
    }

    fun openDocumentTree(view: View) {
        lifecycleScope.launch {
            val documentFile = StoragePublicUtils.SAFHelper.openDocumentTree(this@StorageActivity)
            documentFile?.listFiles()?.forEach {
                Logger.d("openDocumentTree：${it?.uri}")
            }
        }
    }

    fun createDocument(view: View) {
        lifecycleScope.launch {
            Logger.d("createDocument：${StoragePublicUtils.SAFHelper.createDocument(this@StorageActivity, "123.jpg", StoragePublicUtils.MimeType._jpg)}")
        }
    }

    fun deleteDocument(view: View) {
        lifecycleScope.launch {
            Logger.d("deleteDocument：${StoragePublicUtils.SAFHelper.deleteDocument(this@StorageActivity, Uri.parse("content://com.android.providers.downloads.documents/document/1"))}")
        }
    }

    fun captureImage(view: View) {
        lifecycleScope.launch {
            StoragePublicUtils.MediaStoreHelper.captureImage(this@StorageActivity, true)?.let {
                mBinding.iv.setImageBitmap(it)
            }
        }
    }

    fun openFile(view: View) {
        lifecycleScope.launch {
            Logger.e(StoragePublicUtils.MediaStoreHelper.openFile(this@StorageActivity, StoragePublicUtils.MimeType._jpg))
        }
    }

    fun getFiles(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getFiles(this@StorageActivity))
        }
    }

    fun getImages(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getImages(this@StorageActivity))
        }
    }

    fun getAudios(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getAudios(this@StorageActivity))
        }
    }

    fun getVideos(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getVideos(this@StorageActivity))
        }
    }

    fun getDownloads(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getDownloads(this@StorageActivity))
        }
    }

    private var createdFileUri: Uri? = null
    fun createFile(view: View) {
        lifecycleScope.launch {
            createdFileUri = StoragePublicUtils.MediaStoreHelper.createFile(
                    this@StorageActivity,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "33.jpg",
                    "Pictures/like"
            )
            Logger.d(createdFileUri)
        }
    }

    fun deleteFile(view: View) {
        lifecycleScope.launch {
            Logger.d(StoragePublicUtils.MediaStoreHelper.deleteFile(this@StorageActivity, createdFileUri))
        }
    }
}
