package com.like.common.sample.serializable

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivitySerializableBinding
import com.like.common.util.DataStorePreferencesUtil
import com.like.common.util.SPUtils
import com.like.common.util.SerializableUtils
import kotlinx.coroutines.launch

class SerializableActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SerializableActivity"
        private const val KEY_SP_USER1 = "KEY_SP_USER1"
        private const val KEY_SP_USER2 = "KEY_SP_USER2"
        private const val KEY_SP_STRING1 = "KEY_SP_STRING1"
        private const val KEY_SP_STRING2 = "KEY_SP_STRING2"
    }

    private val mBinding: ActivitySerializableBinding by lazy {
        DataBindingUtil.setContentView<ActivitySerializableBinding>(this, R.layout.activity_serializable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        SPUtils.getInstance().init(this)
        SerializableUtils.getInstance().init(this)
        DataStorePreferencesUtil.getInstance().init(this)
    }

    fun get(view: View) {
//        Log.d(TAG, "KEY_SP_USER1：${SerializableUtils.getInstance().get(KEY_SP_USER1, User("like1"))}")
//        Log.d(TAG, "KEY_SP_USER2：${SerializableUtils.getInstance().get<User?>(KEY_SP_USER2, null)}")
//        Log.d(TAG, "KEY_SP_STRING1：${SPUtils.getInstance().get(KEY_SP_STRING1, "string1")}")
//        Log.d(TAG, "KEY_SP_STRING2：${SPUtils.getInstance().get<String?>(KEY_SP_STRING2, null)}")
        lifecycleScope.launch {
            Log.d(TAG, "KEY_SP_STRING1：${DataStorePreferencesUtil.getInstance().get(KEY_SP_STRING1, "string1")}")
        }
    }

    fun put(view: View) {
//        SerializableUtils.getInstance().put(KEY_SP_USER1, null)
//        SerializableUtils.getInstance().put(KEY_SP_USER2, User("like2"))
//        SPUtils.getInstance().put(KEY_SP_STRING1, null)
//        SPUtils.getInstance().put(KEY_SP_STRING1, "1111")
//        SPUtils.getInstance().put(KEY_SP_STRING1, 2222)
//        SPUtils.getInstance().put(KEY_SP_STRING2, "string2")
        lifecycleScope.launch {
            DataStorePreferencesUtil.getInstance().put(KEY_SP_STRING1, "1")
            DataStorePreferencesUtil.getInstance().put(KEY_SP_STRING2, "2")
        }
    }

    fun getAll(view: View) {
//        SerializableUtils.getInstance().getAll()?.forEach {
//            Log.d(TAG, "key=${it.key} value=${it.value as? User}")
//        }
//        SPUtils.getInstance().getAll()?.forEach {
//            Log.d(TAG, "key=${it.key} value=${it.value}")
//        }
        lifecycleScope.launch {
            DataStorePreferencesUtil.getInstance().getAll().forEach {
                Log.d(TAG, "key=${it.key} value=${it.value}")
            }
        }
    }

    fun contains(view: View) {
//        Log.d(TAG, "KEY_SP_USER1：${SerializableUtils.getInstance().contains(KEY_SP_USER1)}")
//        Log.d(TAG, "KEY_SP_USER2：${SerializableUtils.getInstance().contains(KEY_SP_USER2)}")
//        Log.d(TAG, "KEY_SP_STRING1：${SPUtils.getInstance().contains(KEY_SP_STRING1)}")
//        Log.d(TAG, "KEY_SP_STRING2：${SPUtils.getInstance().contains(KEY_SP_STRING2)}")
        lifecycleScope.launch {
            Log.d(TAG, "KEY_SP_STRING1：${DataStorePreferencesUtil.getInstance().contains<String>(KEY_SP_STRING1)}")
        }
    }

    fun clear(view: View) {
//        SerializableUtils.getInstance().clear()
//        SPUtils.getInstance().clear()
        lifecycleScope.launch {
            DataStorePreferencesUtil.getInstance().clear()
        }
    }

    fun remove(view: View) {
//        SerializableUtils.getInstance().remove(KEY_SP_USER1)
//        SPUtils.getInstance().remove(KEY_SP_STRING1)
        lifecycleScope.launch {
            Log.d(TAG, "KEY_SP_STRING1：${DataStorePreferencesUtil.getInstance().remove<String>(KEY_SP_STRING1)}")
        }
    }
}
