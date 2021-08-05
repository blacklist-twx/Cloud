package com.example.forlove.Constant

import android.util.Log

class MyLog {
    companion object {
        @JvmStatic
        fun i(message: String) {
            Log.i(Common.Tag,
                    "[" + Thread.currentThread().getStackTrace()[4].className + "]" + "---" +
                            "[" + Thread.currentThread().getStackTrace()[4].methodName + "]" +
                            ":" + message)
        }
        @JvmStatic
        fun e(message: String) {
            Log.e(Common.Tag,
                    "[" + Thread.currentThread().getStackTrace()[4].className + "]" + "---" +
                            "[" + Thread.currentThread().getStackTrace()[4].methodName + "]" +
                            ":" + message)
        }
    }
}