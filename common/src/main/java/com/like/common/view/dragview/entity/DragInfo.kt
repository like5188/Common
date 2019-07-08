package com.like.common.view.dragview.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * @param originLeft        原始imageview的left
 * @param originTop         原始imageview的top
 * @param originWidth       原始imageview的width
 * @param originHeight      原始imageview的height
 * @param thumbImageUrl     缩略图的url
 * @param imageUrl          原图url
 * @param videoUrl          视频url
 */
class DragInfo(val originLeft: Float,
               val originTop: Float,
               val originWidth: Float,
               val originHeight: Float,
               val thumbImageUrl: String = "",
               val imageUrl: String = "",
               val videoUrl: String = "") : Parcelable {
    // 下面是根据原始尺寸计算出来的辅助尺寸
    var originCenterX: Float = originLeft + originWidth / 2
    var originCenterY: Float = originTop + originHeight / 2

    constructor(parcel: Parcel) : this(
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "") {
        originCenterX = parcel.readFloat()
        originCenterY = parcel.readFloat()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(originLeft)
        parcel.writeFloat(originTop)
        parcel.writeFloat(originWidth)
        parcel.writeFloat(originHeight)
        parcel.writeString(thumbImageUrl)
        parcel.writeString(imageUrl)
        parcel.writeString(videoUrl)
        parcel.writeFloat(originCenterX)
        parcel.writeFloat(originCenterY)
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
