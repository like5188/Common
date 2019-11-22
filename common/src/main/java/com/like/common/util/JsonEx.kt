package com.like.common.util

import org.json.JSONObject

fun JSONObject.optStringIfNullEmpty(name: String): String =
        if (this.isNull(name)) {
            ""
        } else {
            this.optString(name)
        }
