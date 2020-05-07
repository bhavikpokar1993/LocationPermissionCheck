
package com.locationcheck.utils

import android.util.Log
import com.locationcheck.BuildConfig

object JLog {

    private val LOG = BuildConfig.DEBUG
    fun i(string: String) {
        if (LOG) {
            Log.i(TAG, string)
        }
    }

    fun e(string: String) {
        if (LOG) {
            Log.e(TAG, string)
        }
    }

    fun d(string: String) {
        if (LOG) {
            Log.d(TAG, string)
        }
    }

    fun v(string: String) {
        if (LOG) {
            Log.v(TAG, string)
        }
    }

    fun w(string: String) {
        if (LOG) {
            Log.w(TAG, string)
        }
    }

    fun isLog(): Boolean {
        return LOG
    }

}