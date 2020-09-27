package com.like.common.util

import android.os.Parcel
import android.os.Parcelable
import java.sql.Timestamp

/*
Parcel扩展，针对插件无法自动生成的字段：Timestamp、ArrayList
 */

inline fun Parcel.readTimestamp(): Timestamp? {
    return readLong().let { if (it != 0L) Timestamp(it) else null }
}

inline fun Parcel.writeTimestamp(timestamp: Timestamp?) {
    writeLong(timestamp?.time ?: 0L)
}

inline fun <reified T> Parcel.readArrayList(creator: Parcelable.Creator<T>): ArrayList<T>? {
    return createTypedArrayList(creator)
}

inline fun <T : Parcelable> Parcel.writeArrayList(arrayList: ArrayList<T>?) {
    writeTypedList(arrayList)
}