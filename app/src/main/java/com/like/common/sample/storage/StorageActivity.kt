package com.like.common.sample.storage

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityStorageBinding
import com.like.common.util.*
import kotlinx.coroutines.launch
import java.io.FileOutputStream

class StorageActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityStorageBinding>(this, R.layout.activity_storage)
    }

    private val requestPermissionWrapper = RequestPermissionWrapper(this)
    private val startActivityForResultWrapper = StartActivityForResultWrapper(this)
    private val startIntentSenderForResultWrapper = StartIntentSenderForResultWrapper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun openDocument(view: View) {
        lifecycleScope.launch {
            Logger.d("openDocument：${StoragePublicUtils.SAFHelper.openDocument(startActivityForResultWrapper)}")
        }
    }

    fun openDocumentTree(view: View) {
        lifecycleScope.launch {
            val documentFile = StoragePublicUtils.SAFHelper.openDocumentTree(startActivityForResultWrapper)
            documentFile?.listFiles()?.forEach {
                Logger.d("openDocumentTree：${it?.uri}")
            }
        }
    }

    fun createDocument(view: View) {
        lifecycleScope.launch {
            Logger.d("createDocument：${StoragePublicUtils.SAFHelper.createDocument(startActivityForResultWrapper, "123.jpg", StoragePublicUtils.MimeType._jpg)}")
        }
    }

    fun deleteDocument(view: View) {
        lifecycleScope.launch {
            Logger.d("deleteDocument：${StoragePublicUtils.SAFHelper.deleteDocument(this@StorageActivity, Uri.parse("content://com.android.providers.downloads.documents/document/1"))}")
        }
    }

    fun takePhoto(view: View) {
        lifecycleScope.launch {
            StoragePublicUtils.MediaStoreHelper.takePhoto(
                    requestPermissionWrapper,
                    startActivityForResultWrapper,
                    startIntentSenderForResultWrapper,
                    false
            )?.let {
                mBinding.iv.setImageBitmap(it)
            }
        }
    }

    fun selectFile(view: View) {
        lifecycleScope.launch {
            Logger.e(StoragePublicUtils.MediaStoreHelper.selectFile(startActivityForResultWrapper, StoragePublicUtils.MimeType._jpg))
        }
    }

    fun getFiles(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getFiles(requestPermissionWrapper))
        }
    }

    fun getImages(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getImages(requestPermissionWrapper))
        }
    }

    fun getAudios(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getAudios(requestPermissionWrapper))
        }
    }

    fun getVideos(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getVideos(requestPermissionWrapper))
        }
    }

    fun getDownloads(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(StoragePublicUtils.MediaStoreHelper.getDownloads(requestPermissionWrapper))
        }
    }

    private var createdFileUri: Uri? = null
    fun createFile(view: View) {
        lifecycleScope.launch {
            createdFileUri = StoragePublicUtils.MediaStoreHelper.createFile(
                    requestPermissionWrapper,
                    startIntentSenderForResultWrapper,
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    displayName = "21.png",
                    relativePath = "Pictures/like"
            ) {
                FileOutputStream(it?.fileDescriptor).bufferedWriter().use {
                    it.write("0123456789")
                }
            }
            Logger.d(createdFileUri)
        }
    }

    fun updateFileInfo(view: View) {
        lifecycleScope.launch {
            Logger.d(StoragePublicUtils.MediaStoreHelper.updateFile(
                    requestPermissionWrapper,
                    startIntentSenderForResultWrapper,
                    createdFileUri,
                    displayName = "22.png",
                    relativePath = Environment.DIRECTORY_PICTURES
            ))
        }
    }

    fun deleteFile(view: View) {
        lifecycleScope.launch {
            Logger.d(StoragePublicUtils.MediaStoreHelper.deleteFile(requestPermissionWrapper, startIntentSenderForResultWrapper, createdFileUri))
        }
    }
}
