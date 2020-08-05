package com.like.common.view.callback

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData

internal class LiveDataCallbackFragment : androidx.fragment.app.Fragment() {
    companion object {
        private const val REQUEST_CODE = 88
    }

    private var mLiveData: MutableLiveData<Callback>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setRetainInstance(true)，可保留fragment实例，使它不经历onDestroy()和onCreate()方法，在横竖屏切换时，保证数据和状态不变。
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            mLiveData?.value = Callback(resultCode, data)
        }
    }

    fun startActivityForResult(liveData: MutableLiveData<Callback>, intent: Intent) {
        mLiveData = liveData
        startActivityForResult(intent, REQUEST_CODE)
    }

}