package com.like.common.view.dragview.entity

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * @param originLeft        原始imageview的left
 * @param originTop         原始imageview的top
 * @param originWidth       原始imageview的width
 * @param originHeight      原始imageview的height
 * @param thumbnailUrl      缩略图的url
 * @param imageUrl          原图url
 * @param videoUrl          视频url
 */
class DragInfo(val originLeft: Float,
               val originTop: Float,
               val originWidth: Float,
               val originHeight: Float,
               val thumbnailUrl: String = "",
               val imageUrl: String = "",
               val videoUrl: String = "") : Parcelable {

    fun getInitScaleX(view: View) = originWidth / view.width.toFloat()

    fun getInitScaleY(view: View) = originHeight / view.height.toFloat()

    fun getInitTranslationX(view: View): Float {
        val originCenterX: Float = originLeft + originWidth / 2
        return originCenterX - view.width / 2
    }

    fun getInitTranslationY(view: View): Float {
        val originCenterY: Float = originTop + originHeight / 2
        return originCenterY - view.height / 2
    }

    constructor(parcel: Parcel) : this(
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(originLeft)
        parcel.writeFloat(originTop)
        parcel.writeFloat(originWidth)
        parcel.writeFloat(originHeight)
        parcel.writeString(thumbnailUrl)
        parcel.writeString(imageUrl)
        parcel.writeString(videoUrl)
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
