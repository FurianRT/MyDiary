package com.furianrt.mydiary.general

import android.widget.ImageView
import com.furianrt.mydiary.R
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader

class MediaLoader : AlbumLoader {

    override fun load(imageView: ImageView?, albumFile: AlbumFile?) {
        load(imageView, albumFile?.path)
    }

    override fun load(imageView: ImageView?, url: String?) {
        GlideApp.with(imageView!!.context)
                .load(url)
                .placeholder(R.drawable.ic_image)
                .override(250, 250)
                .centerCrop()
                .into(imageView)
    }
}