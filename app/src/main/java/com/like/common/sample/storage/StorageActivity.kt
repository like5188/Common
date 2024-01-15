package com.like.common.sample.storage

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityStorageBinding
import com.like.common.util.Logger
import com.like.common.util.UriUtils
import com.like.common.util.activityresultlauncher.requestPermission
import com.like.common.util.context
import com.like.common.util.storage.external.MediaStoreUtils
import com.like.common.util.storage.external.SafUtils
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
            Logger.d("openDocument：${SafUtils.openDocument(this@StorageActivity)}")
        }
    }

    fun openDocumentTree(view: View) {
        lifecycleScope.launch {
            val documentFile = SafUtils.openDocumentTree(this@StorageActivity)
            documentFile?.listFiles()?.forEach {
                Logger.d("openDocumentTree：${it?.uri}")
            }
        }
    }

    private var documentUri: Uri? = null
    fun createDocument(view: View) {
        lifecycleScope.launch {
            documentUri = SafUtils.createDocument(this@StorageActivity, "123.jpg", SafUtils.MimeType._jpg)
            Logger.d("createDocument：$documentUri")
        }
    }

    fun deleteDocument(view: View) {
        lifecycleScope.launch {
            Logger.d(
                "deleteDocument：${SafUtils.deleteDocument(this@StorageActivity, documentUri)}"
            )
        }
    }

    fun selectMultiFile(view: View) {
        lifecycleScope.launch {
            val uriList = SafUtils.selectMultiFile(this@StorageActivity, SafUtils.MimeType._0)
            uriList.forEach {
                Logger.e(it)
            }
        }
    }

    fun selectFile(view: View) {
        lifecycleScope.launch {
            Logger.e(SafUtils.selectFile(this@StorageActivity, SafUtils.MimeType._0))
        }
    }

    fun takePhoto(view: View) {
        lifecycleScope.launch {
            MediaStoreUtils.takePhoto(
                this@StorageActivity, false
            )?.let {
                mBinding.iv.setImageBitmap(it)
            }
        }
    }

    fun getFiles(view: View) {
        lifecycleScope.launch {
            if (requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Logger.printCollection(MediaStoreUtils.getFiles(this@StorageActivity))
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getImages(view: View) {
        lifecycleScope.launch {
            if (requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                val images = MediaStoreUtils.getImages(this@StorageActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&// android 10 及其以上
                    !Environment.isExternalStorageLegacy() &&// 开启了分区存储
                    requestPermission(Manifest.permission.ACCESS_MEDIA_LOCATION)
                ) {
                    images.forEach {
                        val latLong = UriUtils.getLatLongFromUri(this@StorageActivity, it.uri)
                        if (latLong != null && latLong.size >= 2) {
                            it.latitude = latLong[0]
                            it.longitude = latLong[1]
                        }
                    }
                }
                Logger.printCollection(images)
            }
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

    private var createdFileUri: Uri? = null
    fun createFile(view: View) {
        lifecycleScope.launch {
            createdFileUri = MediaStoreUtils.createFile(
                this@StorageActivity,
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                displayName = "6.jpg",
                relativePath = "${Environment.DIRECTORY_PICTURES}/like"
            ) {
                it?.write(context.assets.open("image_2.jpg").readBytes())
            }
            Logger.d(createdFileUri)
        }
    }

    fun updateFile(view: View) {
        val uri = createdFileUri ?: return
        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Logger.d(
                    MediaStoreUtils.updateFile(
                        this@StorageActivity, uri, displayName = "22", relativePath = "${Environment.DIRECTORY_PICTURES}/like1"
                    )
                )
            }
        }
    }

    fun deleteFile(view: View) {
        val uri = createdFileUri ?: return
        lifecycleScope.launch {
            Logger.d(MediaStoreUtils.deleteFile(this@StorageActivity, uri))
        }
    }
}
