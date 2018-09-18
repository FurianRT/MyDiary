package com.furianrt.mydiary.general

import android.widget.ImageView
import com.furianrt.mydiary.R
import com.squareup.picasso.Picasso
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader
import java.io.File

class MediaLoader : AlbumLoader {

    override fun load(imageView: ImageView?, albumFile: AlbumFile?) {
        load(imageView, albumFile?.path)
    }

    override fun load(imageView: ImageView?, url: String?) {
        Picasso.get()
                .load(File(url))
                .placeholder(R.drawable.ic_add_a_photo)
                .resize(250, 250)
                .centerCrop()
                .into(imageView)
    }
}