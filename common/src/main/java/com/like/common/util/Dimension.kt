package com.like.common.util

import android.content.res.Resources
import android.util.TypedValue

inline val Float.dp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

inline val Int.dp: Int
    get() = this.toFloat().dp.toInt()

inline val Float.sp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)

inline val Int.sp: Int
    get() = this.toFloat().sp.toInt()