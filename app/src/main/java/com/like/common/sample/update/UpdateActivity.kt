package com.like.common.sample.update

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.MainActivity
import com.like.common.sample.databinding.ActivityUpdateBinding
import com.like.common.util.AppUtils
import com.like.common.util.PermissionUtils
import com.like.common.view.update.Update
import com.like.common.view.update.shower.ForceUpdateDialogShower
import com.like.common.view.update.shower.NotificationShower

/**
 * 更新测试
 */
class UpdateActivity : AppCompatActivity() {
    private val mBinding: ActivityUpdateBinding by lazy {
        DataBindingUtil.setContentView<ActivityUpdateBinding>(this, com.like.common.sample.R.layout.activity_update)
    }

    private val mPermissionUtils: PermissionUtils by lazy {
        PermissionUtils().apply {
            init(this@UpdateActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        Update.with(this).setDownloader(RetrofitDownloader(application))
    }

    @SuppressLint("MissingPermission")
    fun update(view: View) {
        mPermissionUtils.checkPermissions(
                {
                    val updateInfo = UpdateInfo().apply {
                        // 是否需要更新。0：不需要；1：需要；2：必须更新
                        isUpdate = 1
                        versionName = "1.0"
                        versionCode = 1
                        downUrl = "http://shouji.360tpcdn.com/181222/f31d56919d5dfbdb479a8e5746a75146/com.snda.wifilocating_181219.apk"
                        message = "bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改"
                    }
                    when (updateInfo.isUpdate) {
                        // 需要更新
                        1 -> AlertDialog.Builder(this)
                                .setTitle("发现新版本，是否更新？")
                                .setMessage(updateInfo.message)
                                .setCancelable(false)
                                .setPositiveButton("马上更新") { dialog, _ ->
                                    // 开始更新
                                    Update.with(this).url(updateInfo.downUrl)
                                            .versionName(updateInfo.versionName)
                                            .shower(
                                                    NotificationShower(
                                                            this,
                                                            com.like.common.sample.R.mipmap.ic_launcher,
                                                            PendingIntent.getActivity(
                                                                    this,
                                                                    2,
                                                                    Intent(this, MainActivity::class.java),
                                                                    PendingIntent.FLAG_UPDATE_CURRENT
                                                            ),
                                                            "a",
                                                            "更新"
                                                    )
                                            )
                                            .download()
                                    dialog.dismiss()
                                }
                                .setNegativeButton("下次再说") { dialog, _ ->
                                    // 需要更新，但是不更新
                                    dialog.dismiss()
                                }
                                .show()
                    }
                }, {}, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    @SuppressLint("MissingPermission")
    fun forceUpdate(view: View) {
        mPermissionUtils.checkPermissions(
                {
                    val updateInfo = UpdateInfo().apply {
                        // 是否需要更新。0：不需要；1：需要；2：必须更新
                        isUpdate = 2
                        versionName = "1.0"
                        versionCode = 1
                        downUrl = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk"
                        message = "bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改bug修改"
                    }
                    when (updateInfo.isUpdate) {
                        // 必须强制更新
                        2 -> AlertDialog.Builder(this)
                                .setTitle("发现新版本，您必须更新后才能继续使用！")
                                .setMessage(updateInfo.message)
                                .setCancelable(false)
                                .setPositiveButton("马上更新") { dialog, _ ->
                                    // 开始更新
                                    Update.with(this).url(updateInfo.downUrl)
                                            .versionName(updateInfo.versionName)
                                            .shower(ForceUpdateDialogShower(this.supportFragmentManager))
                                            .download()
                                    dialog.dismiss()
                                }.setNegativeButton("暂不使用") { dialog, _ ->
                                    // 需要强制更新，但是不更新
                                    dialog.dismiss()
                                    AppUtils.getInstance(this@UpdateActivity).exitApp()
                                }
                                .show()
                    }
                }, {}, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

}
