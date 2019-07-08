package com.like.common.sample.update

class UpdateInfo {
    /**
     * 是否需要更新。0：不需要；1：需要；2：必须更新
     */
    var isUpdate: Int = 0
    /**
     * 服务器端的版本号
     */
    var versionName: String = ""
    /**
     * 服务器端的版本
     */
    var versionCode: Int = 0
    /**
     * apk下载地址
     */
    var downUrl: String = ""
    /**
     * 版本更新信息
     */
    var message: String = ""
}