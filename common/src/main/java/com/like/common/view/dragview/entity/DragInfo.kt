package com.like.common.view.dragview.entity

import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * @param originRect        原始 imageview 的 RectF
 * @param thumbUrl          缩略图的 url
 * @param url               原图或者视频的 url
 */
data class DragInfo(
    val originRect: Rect,
    val thumbUrl: String = "",
    val url: String = ""
) : Parcelable {

    fun getInitScaleX(view: View) = originRect.width() / view.width.toFloat()

    fun getInitScaleY(view: View) = originRect.height() / view.height.toFloat()

    fun getInitTranslationX(view: View): Float {
        val originCenterX: Float = originRect.left + originRect.width() / 2f
        return originCenterX - view.width / 2
    }

    fun getInitTranslationY(view: View): Float {
        val originCenterY: Float = originRect.top + originRect.height() / 2f
        return originCenterY - view.height / 2
    }

    constructor(parcel: Parcel) : this(
        parcel.readParcelable<Rect>(Rect::class.java.classLoader)!!,
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(originRect, flags)
        parcel.writeString(thumbUrl)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DragInfo> {
        override fun createFromParcel(parcel: Parcel): DragInfo {
            return DragInfo(parcel)
        }

        override fun newArray(size: Int): Array<DragInfo?> {
            return arrayOfNulls(size)
        }
    }


}
