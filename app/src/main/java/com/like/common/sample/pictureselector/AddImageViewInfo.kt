package com.like.common.sample.pictureselector

import com.like.common.R
import com.like.livedatarecyclerview.model.IItem
import com.luck.picture.lib.entity.LocalMedia

class AddImageViewInfo(val localMedia: LocalMedia, val des: String) : IItem {
    override var layoutId: Int = R.layout.view_image
    override var variableId: Int = -1
    val imagePath: String = localMedia.path
    val compressImagePath: String = localMedia.compressPath

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddImageViewInfo

        if (imagePath != other.imagePath) return false

        return true
    }

    override fun hashCode(): Int {
        return imagePath.hashCode()
    }

}