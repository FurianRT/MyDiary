package com.furianrt.mydiary.screens.gallery

import com.furianrt.mydiary.data.DataManager
import javax.inject.Inject

class GalleryActivityPresenter @Inject constructor(
        private val dataManager: DataManager
) : GalleryActivityContract.Presenter()