package com.like.common.sample.pictureselector

import android.Manifest
import androidx.databinding.ObservableBoolean
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.GlideUtils
import com.like.common.util.PermissionUtils
import com.like.common.util.VibrateUtils
import com.like.common.sample.databinding.ViewAddImageBinding
import com.like.common.sample.databinding.ViewImageBinding
import com.like.livedatarecyclerview.adapter.BaseAddImageViewAdapter
import com.like.livedatarecyclerview.model.IRecyclerViewItem
import com.like.livedatarecyclerview.viewholder.CommonViewHolder
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia

class MyAddImageViewAdapter(private val activity: androidx.fragment.app.FragmentActivity, recyclerView: androidx.recyclerview.widget.RecyclerView, @DrawableRes addImageResId: Int)
    : BaseAddImageViewAdapter(recyclerView, AddInfo(addImageResId), 9) {
    private val deleteButtonShown: ObservableBoolean = ObservableBoolean()
    private val mGlideUtils = GlideUtils(activity)

    private val mPermissionUtils: PermissionUtils by lazy {
        PermissionUtils(activity)
    }

    fun add(localMedias: List<LocalMedia>) {
        // 去掉已经添加过的
        val items = getItemsExceptPlus()
        val selectList = localMedias.map {
            AddImageViewInfo(it, "des")
        }.filter { !items.contains(it) }
        addItems(selectList)
    }

    private fun getLocalMedias() = getItemsExceptPlus().map {
        (it as AddImageViewInfo).localMedia
    }

    override fun bindOtherVariable(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {
        when (item) {
            is AddInfo -> {// +号图片
                val binding = holder.binding as ViewAddImageBinding
                binding.iv.setImageResource(item.addImageResId)
                binding.iv.setOnClickListener {

                    mPermissionUtils.checkPermissions(
                            {
                                PictureSelector.create(activity)
                                        .openGallery(PictureMimeType.ofImage())
                                        .maxSelectNum(9)
                                        .selectionMedia(getLocalMedias())
                                        .imageSpanCount(3)// 每行显示个数 int
                                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                        .previewImage(true)// 是否可预览图片 true or false
                                        .isCamera(true)// 是否显示拍照按钮 true or false
                                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                                        .compress(true)// 是否压缩 true or false
                                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                                        .forResult(PictureConfig.CHOOSE_REQUEST)
                            }, {}, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    // 隐藏删除按钮
                    deleteButtonShown.set(false)
                }
            }
            is AddImageViewInfo -> {// 添加的图片
                val binding = holder.binding as ViewImageBinding
                mGlideUtils.display(item.compressImagePath, binding.iv)
                binding.tv.text = item.des
                binding.root.setOnLongClickListener {
                    // 显示删除按钮
                    if (!deleteButtonShown.get()) {
                        VibrateUtils.vibrate(activity, 300)// 震动300毫秒
                        deleteButtonShown.set(true)
                    }
                    true
                }
                binding.root.setOnClickListener {
                    PictureSelector.create(activity).externalPicturePreview(position, getLocalMedias())
                }
                binding.ivDelete.setOnClickListener {
                    removeItem(item)
                }
                binding.deleteButtonShown = deleteButtonShown
            }
        }
    }

}