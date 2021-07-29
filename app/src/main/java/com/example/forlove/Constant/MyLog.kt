package com.example.forlove.Constant

import android.util.Log

class MyLog {
    companion object {
        @JvmStatic
        fun i(message: String) {
            Log.i(Constant.Tag,
                    "[" + Thread.currentThread().getStackTrace()[2].className + "]" + "---" +
                            "[" + Thread.currentThread().getStackTrace()[2].methodName + "]" +
                            ":" + message)
        }
    }
}