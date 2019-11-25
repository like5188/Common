package com.like.common.view.callback

import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.reactivex.subjects.PublishSubject

internal class RxCallbackFragment : androidx.fragment.app.Fragment() {
    companion object {
        private const val REQUEST_CODE = 88
    }

    private var mPublishSubject: PublishSubject<Callback>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("RxCallbackFragment", "RxCallbackFragment create")
        // 调用setRetainInstance(true)方法可保留fragment
        // 已保留的fragment不会随着activity一起被销毁；
        // 相反，它会一直保留(进程不消亡的前提下)，并在需要时原封不动地传递给新的Activity。
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("RxCallbackFragment", "onActivityResult $this")
        if (requestCode == REQUEST_CODE) {
            Log.d("RxCallbackFragment", "onActivityResult success")
            mPublishSubject?.onNext(Callback(resultCode, data))
        }
        mPublishSubject?.onComplete()
    }

    fun startActivityForResult(subject: PublishSubject<Callback>, intent: Intent) {
        mPublishSubject = subject
        startActivityForResult(intent, REQUEST_CODE)
    }

}