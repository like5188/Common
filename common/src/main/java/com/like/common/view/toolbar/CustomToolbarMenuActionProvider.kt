package com.like.common.view.toolbar

import android.content.Context
import android.view.View
import androidx.core.view.ActionProvider

/**
 * Menu 菜单的自定义视图提供者
 */
class CustomToolbarMenuActionProvider(context: Context) : ActionProvider(context), ICustomToolbarMenu by CustomToolbarMenu(context) {

    override fun onCreateActionView(): View {
        return getView()
    }

}
