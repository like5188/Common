package com.like.common.util

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
