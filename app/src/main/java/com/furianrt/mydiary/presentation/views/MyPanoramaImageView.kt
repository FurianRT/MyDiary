package com.furianrt.mydiary.presentation.views

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import com.gjiazhe.panoramaimageview.PanoramaImageView

class MyPanoramaImageView : PanoramaImageView {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun getOrientation(): Byte =
            if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                when (super.getOrientation()) {
                    ORIENTATION_HORIZONTAL -> {
                        isInvertScrollDirection = false
                        ORIENTATION_VERTICAL
                    }
                    ORIENTATION_VERTICAL -> {
                        isInvertScrollDirection = true
                        ORIENTATION_HORIZONTAL
                    }
                    else -> {
                        isInvertScrollDirection = true
                        ORIENTATION_NONE
                    }
                }
            } else {
                super.getOrientation()
            }
}