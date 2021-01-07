package com.like.common.sample.autowired

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.like.common.util.AutoWired
import com.like.common.util.Logger
import com.like.common.util.injectForIntentExtras

/**
 * Activity 相关的测试
 */
class AutoWiredActivity : AppCompatActivity() {
    // 测试正确
    @AutoWired
    private var param1: Int? = null

    // 测试val--正确，并且可以赋值为 null。所以参数类型必须设置为 null，否则会出现 NPE
    @AutoWired
    private val param2: String = ""

    // 测试参数名字错误--警告：@AutoWired field com.like.common.sample.autowired.AutoWiredActivity.param3 not found
    @AutoWired
    private var param3: String? = null

    // 测试类型错误--抛异常
    @AutoWired
    private var param4: Int? = null

    // 测试Parcelable集合
    @AutoWired
    private var param5: List<P>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectForIntentExtras()
        Logger.d("param1=$param1")
        Logger.d("param2=$param2")
        Logger.d("param3=$param3")
        Logger.d("param4=$param4")
        Logger.printCollection(param5)
//        Logger.e(param2.toString())// NPE
    }

    data class P(val name: String) : Parcelable {

        constructor(parcel: Parcel) : this(parcel.readString() ?: "") {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<P> {
            override fun createFromParcel(parcel: Parcel): P {
                return P(parcel)
            }

            override fun newArray(size: Int): Array<P?> {
                return arrayOfNulls(size)
            }
        }
    }

}
