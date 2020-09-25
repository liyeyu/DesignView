package com.yuema.designview.view

import android.content.Context

/**
 * @author liyeyu
 * @date 2020/9/24
 * description
 */
class Utils {

    companion object{
        fun dp2px(context: Context, dipValue: Float): Int {
            val scale: Float = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }
    }
}