package com.like.common.sample.pictureselector

import androidx.annotation.DrawableRes
import com.like.common.sample.R
import com.like.livedatarecyclerview.model.IItem

/**
 * +号视图需要的数据
 */
data class AddInfo(@DrawableRes val addImageResId: Int) : IItem {
    override var layoutId: Int = R.layout.view_add_image
    override var variableId: Int = -1
}