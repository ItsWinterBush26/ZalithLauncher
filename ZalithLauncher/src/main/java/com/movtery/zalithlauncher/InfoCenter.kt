package com.movtery.zalithlauncher

import android.content.Context
import com.movtery.zalithlauncher.InfoDistributor.APP_NAME

class InfoCenter {
    companion object {
        const val QQ_GROUP: String = "435667089"

        @JvmStatic
        fun replaceName(context: Context, resString: Int): String = context.getString(resString, APP_NAME)
    }
}
