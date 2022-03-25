package com.like.common.util.storage.external

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import com.like.common.util.StartActivityForResultLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

// 分区存储改变了应用在设备的外部存储设备中存储和访问文件的方式。
/**
 * 外部存储公共目录 操作其它文件（pdf、office、doc、txt、下载的文件等）的工具类。
 * 外部存储公共目录：应用卸载后，文件不会删除。
 * /storage/emulated/(0/1/...)/MediaStore.Downloads
 *
 * 权限：不需要申请存储权限
 *
 * 访问方式：Storage Access Framework (不需要申请存储权限) Android 4.4（API 级别 19）引入，由用户自己通过系统选择器来操作文件。
 *      1、在Android 4.4 之前，如果想从另外一个App中选择一个文件（比如从图库中选择一张图片文件）必须触发一个ACTION为ACTION_PICK或者ACTION_GET_CONTENT的Intent，再在候选的App中选择一个App，从中获得你想要的文件，最关键的是被选择的App中要具有能为你提供文件的功能，但如果一个不负责任的第三方开发者注册了一个恰恰符合你需求的Intent，但是没有实现返回文件的功能，那么就会出现意想不到的错误。
 *      2、Android 4.4中引入了Storage Access Framework存储访问框架（SAF）。SAF为用户浏览手机中存储的内容（不仅包括文档、图片，视频、音频、下载、GoogleDrive等，还包括所有继承自DocumentsProvider的特定云存储、本地存储提供的内容）提供了统一的管理和展现形式
 *      无论内容来自于哪里，是哪个应用调用浏览系统文件内容的命令，SAF都会用一个统一的界面（DocumentsUI App）让你去使用，通过发送Intent.ACTION_OPEN_DOCUMENT的 Intent来弹出一个很漂亮的界面
 *
 *      主要角色成员包括：
 *      1、Document Provider 文件存储服务提供者。
 *          Document Provider让一个存储服务（比如Google Drive）可以对外以统一的形式展示自己所管理的文件，一个Document Provider代码上就是实现了DocumentsProvider.java的子类
 *      2、DocumentsUI 文件存储选择器App
 *
 * Android 存储用例和最佳做法：https://developer.android.google.cn/training/data-storage/use-cases
 */
object SafUtils {

    /**
     * 使用系统选择器选择指定类型的文件
     */
    suspend fun selectFile(startActivityForResultLauncher: StartActivityForResultLauncher, mimeType: MimeType = MimeType._0): Intent? {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = mimeType.value
        return startActivityForResultLauncher.launch(intent).data
    }

    /**
     * 打开文档或文件
     *
     * 注意：
     *  1 在Android 11上，无法从以下目录中选择单独的文件。Android/data/ 目录及其所有子目录。Android/obb/ 目录及其所有子目录。
     *  2 ACTION_OPEN_DOCUMENT 并非用于替代 ACTION_GET_CONTENT。您应使用的 intent 取决于应用的需要：
     *      如果您只想让应用读取/导入数据，请使用 ACTION_GET_CONTENT。使用此方法时，应用会导入数据（如图片文件）的副本。
     *      如果您想让应用获得对文档提供程序所拥有文档的长期、持续访问权限，请使用 ACTION_OPEN_DOCUMENT。例如，照片编辑应用可让用户编辑存储在文档提供程序中的图片。
     *
     * @param pickerInitialUri      文件选择器中初始显示的文件夹。默认为 null，会显示 Downloads 目录。api 26 以上有效。
     * 可以设置其它目录，比如：Uri.parse("content://com.android.externalstorage.documents/document/primary:Pictures%2flike")
     * 固定格式：content://com.android.externalstorage.documents/document/primary
     * :Pictures 代表下面的 Pictures 文件夹，当然如果再想得到下一级文件夹 like 还需要:既 :Pictures%2flike
     * @return  返回的 Uri 为文件的
     */
    suspend fun openDocument(
        startActivityForResultLauncher: StartActivityForResultLauncher,
        mimeType: MimeType = MimeType._0,
        pickerInitialUri: Uri? = null
    ): Uri? {
        //通过系统的文件浏览器选择一个文件
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        //筛选，只显示可以“打开”的结果，如文件(而不是联系人或时区列表)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        //过滤只显示指定类型文件
        intent.type = mimeType.value
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        return startActivityForResultLauncher.launch(intent).data?.data
    }

    /**
     * 授予对目录内容的访问权限
     *
     * 注意：在Android 11上，无法通过SAF选择External Storage根目录、Downloads目录以及App专属目录(Android/data、Android/obb)
     *
     * @return  返回文件夹 DocumentFile
     */
    suspend fun openDocumentTree(
        startActivityForResultLauncher: StartActivityForResultLauncher,
        pickerInitialUri: Uri? = null
    ): DocumentFile? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return null
        }
        //通过系统的文件浏览器选择一个文件
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        val treeUri = startActivityForResultLauncher.launch(intent).data?.data ?: return null
        return DocumentFile.fromTreeUri(startActivityForResultLauncher.activity.applicationContext, treeUri)
    }

    /**
     * 创建新文件
     *
     * 注意：ACTION_CREATE_DOCUMENT 无法覆盖现有文件。如果您的应用尝试保存同名文件，系统会在文件名的末尾附加一个数字并将其包含在一对括号中。
     *
     * @return  返回的 Uri 为文件的
     */
    suspend fun createDocument(
        startActivityForResultLauncher: StartActivityForResultLauncher,
        fileName: String,
        mimeType: MimeType = MimeType._0,
        pickerInitialUri: Uri? = null
    ): Uri? {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        // Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mimeType.value// 文件类型
        intent.putExtra(Intent.EXTRA_TITLE, fileName)// 文件名称
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        return startActivityForResultLauncher.launch(intent).data?.data
    }

    /**
     * 删除文件
     */
    suspend fun deleteDocument(context: Context, uri: Uri?): Boolean {
        uri ?: return false
        return withContext(Dispatchers.IO) {
            try {
                DocumentsContract.deleteDocument(context.applicationContext.contentResolver, uri)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    /**
     * 是否虚拟文件
     *
     * 在较早的 Android 版本中，您的应用可以使用存储访问框架来允许用户从他们的云存储帐户中选择文件，如 Google Drive。
     * 但是，不能表示没有直接字节码表示的文件；每个文件都必须提供一个输入流。
     * Android 7.0 在存储访问框架中添加了虚拟文件的概念。
     *
     * 注意：由于应用无法使用 openInputStream() 方法直接打开虚拟文件，因此在创建包含 ACTION_OPEN_DOCUMENT 或 ACTION_OPEN_DOCUMENT_TREE 操作的 intent 时，请勿使用 CATEGORY_OPENABLE 类别。
     */
    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun isVirtualFile(context: Context, uri: Uri): Boolean {
        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return false
        }

        return withContext(Dispatchers.IO) {
            val cursor: Cursor? = context.applicationContext.contentResolver.query(
                uri,
                arrayOf(DocumentsContract.Document.COLUMN_FLAGS),
                null,
                null,
                null
            )

            val flags: Int = cursor?.use {
                if (cursor.moveToFirst()) {
                    cursor.getInt(0)
                } else {
                    0
                }
            } ?: 0

            flags and DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT != 0
        }
    }

    /**
     * 在验证文档为虚拟文件后，您可以将其强制转换为另一种 MIME 类型，例如 "image/png"。
     * 以下代码段展示了如何查看某个虚拟文件是否可以表示为图片，如果可以，则从该虚拟文件获取输入流：
     */
    @Throws(IOException::class)
    fun getInputStreamForVirtualFile(context: Context, uri: Uri, mimeTypeFilter: String): InputStream? {
        val openableMimeTypes: Array<String>? = context.applicationContext.contentResolver.getStreamTypes(uri, mimeTypeFilter)

        return if (openableMimeTypes?.isNotEmpty() == true) {
            context.applicationContext.contentResolver
                .openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null)
                ?.createInputStream()
        } else {
            throw FileNotFoundException()
        }
    }

    enum class MimeType(val value: String) {
        _apk("application/vnd.android.package-archive"),
        _doc("application/msword"),
        _docx("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        _xls("application/vnd.ms-excel"),
        _xlsx("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        _exe("application/octet-stream"),
        _gtar("application/x-gtar"),
        _gz("application/x-gzip"),
        _bin("application/octet-stream"),
        _class("application/octet-stream"),
        _jar("application/java-archive"),
        _js("application/x-javascript"),
        _mpc("application/vnd.mpohun.certificate"),
        _msg("application/vnd.ms-outlook"),
        _pdf("application/pdf"),
        _pps("application/vnd.ms-powerpoint"),
        _ppt("application/vnd.ms-powerpoint"),
        _pptx("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        _rtf("application/rtf"),
        _tar("application/x-tar"),
        _tgz("application/x-compressed"),
        _wps("application/vnd.ms-works"),
        _z("application/x-compress"),
        _zip("application/x-zip-compressed"),
        _png("image/png"),
        _jpeg("image/jpeg"),
        _jpg("image/jpeg"),
        _webp("image/webp"),
        _bmp("image/bmp"),
        _gif("image/gif"),
        _m3u("audio/x-mpegurl"),
        _m4a("audio/mp4a-latm"),
        _m4b("audio/mp4a-latm"),
        _m4p("audio/mp4a-latm"),
        _mp2("audio/x-mpeg"),
        _mp3("audio/x-mpeg"),
        _mpga("audio/mpeg"),
        _ogg("audio/ogg"),
        _rmvb("audio/x-pn-realaudio"),
        _wav("audio/x-wav"),
        _wma("audio/x-ms-wma"),
        _wmv("audio/x-ms-wmv"),
        _prop("text/plain"),
        _rc("text/plain"),
        _c("text/plain"),
        _conf("text/plain"),
        _cpp("text/plain"),
        _h("text/plain"),
        _htm("text/html"),
        _html("text/html"),
        _java("text/plain"),
        _log("text/plain"),
        _sh("text/plain"),
        _txt("text/plain"),
        _xml("text/plain"),
        _3gp("video/3gpp"),
        _asf("video/x-ms-asf"),
        _avi("video/x-msvideo"),
        _m4u("video/vnd.mpegurl"),
        _m4v("video/x-m4v"),
        _mov("video/quicktime"),
        _mp4("video/mp4"),
        _mpe("video/mpeg"),
        _mpeg("video/mpeg"),
        _mpg("video/mpeg"),
        _mpg4("video/mp4"),
        _0("*/*");

        companion object {
            fun isApk(mimeType: String?): Boolean = _apk.value == mimeType

            fun isImage(mimeType: String?): Boolean = mimeType?.startsWith("image/") == true

            fun isGif(mimeType: String?): Boolean = _gif.value == mimeType

            fun isAudio(mimeType: String?): Boolean = mimeType?.startsWith("audio/") == true

            fun isVideo(mimeType: String?): Boolean = mimeType?.startsWith("video/") == true

            fun isText(mimeType: String?): Boolean = mimeType?.startsWith("text/") == true
        }
    }

}
