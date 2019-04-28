package com.furianrt.mydiary.screens.main.fragments.imagesettings

import com.furianrt.mydiary.data.DataManager

class ImageSettingsPresenter(
        private val mDataManager: DataManager
) : ImageSettingsContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.close()
    }
}