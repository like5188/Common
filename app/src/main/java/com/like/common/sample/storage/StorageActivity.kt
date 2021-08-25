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
import com.like.common.util.storage.external.MediaUtils
import com.like.common.util.storage.external.SafUtils
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
            Logger.d("openDocument：${SafUtils.openDocument(startActivityForResultWrapper)}")
        }
    }

    fun openDocumentTree(view: View) {
        lifecycleScope.launch {
            val documentFile = SafUtils.openDocumentTree(startActivityForResultWrapper)
            documentFile?.listFiles()?.forEach {
                Logger.d("openDocumentTree：${it?.uri}")
            }
        }
    }

    fun createDocument(view: View) {
        lifecycleScope.launch {
            Logger.d("createDocument：${SafUtils.createDocument(startActivityForResultWrapper, "123.jpg", SafUtils.MimeType._jpg)}")
        }
    }

    fun deleteDocument(view: View) {
        lifecycleScope.launch {
            Logger.d(
                "deleteDocument：${
                    SafUtils.deleteDocument(
                        this@StorageActivity,
                        Uri.parse("content://com.android.providers.downloads.documents/document/1")
                    )
                }"
            )
        }
    }

    fun selectFile(view: View) {
        lifecycleScope.launch {
            Logger.e(SafUtils.selectFile(startActivityForResultWrapper, SafUtils.MimeType._jpg))
        }
    }

    fun takePhoto(view: View) {
        lifecycleScope.launch {
            MediaUtils.takePhoto(
                requestPermissionWrapper,
                startActivityForResultWrapper,
                startIntentSenderForResultWrapper,
                false
            )?.let {
                mBinding.iv.setImageBitmap(it)
            }
        }
    }

    fun getFiles(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(MediaUtils.getFiles(requestPermissionWrapper))
        }
    }

    fun getImages(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(MediaUtils.getImages(requestPermissionWrapper))
        }
    }

    fun getAudios(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(MediaUtils.getAudios(requestPermissionWrapper))
        }
    }

    fun getVideos(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(MediaUtils.getVideos(requestPermissionWrapper))
        }
    }

    fun getDownloads(view: View) {
        lifecycleScope.launch {
            Logger.printCollection(MediaUtils.getDownloads(requestPermissionWrapper))
        }
    }

    private var createdFileUri: Uri? = null
    fun createFile(view: View) {
        lifecycleScope.launch {
            createdFileUri = MediaUtils.createFile(
                requestPermissionWrapper,
                startIntentSenderForResultWrapper,
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                displayName = "21.png",
                relativePath = "${Environment.DIRECTORY_PICTURES}/like"
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
            Logger.d(
                MediaUtils.updateFile(
                    requestPermissionWrapper,
                    startIntentSenderForResultWrapper,
                    createdFileUri,
                    displayName = "22.png",
                    relativePath = Environment.DIRECTORY_PICTURES
                )
            )
        }
    }

    fun deleteFile(view: View) {
        lifecycleScope.launch {
            Logger.d(MediaUtils.deleteFile(requestPermissionWrapper, startIntentSenderForResultWrapper, createdFileUri))
        }
    }
}
