package com.like.common.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

val String?.md5: String
    get() {
        if (this.isNullOrEmpty()) {
            return ""
        }
        return try {
            val messageDigest = MessageDigest.getInstance("MD5")
            val digestBytes = messageDigest.digest(toByteArray())
            val sb = StringBuilder()
            for (b in digestBytes) {
                sb.append(String.format("%02x", b))
            }
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            ""
        }
    }