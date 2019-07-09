package com.furianrt.mydiary.screens.main.fragments.imagesettings

import com.furianrt.mydiary.data.DataManager
import javax.inject.Inject

class ImageSettingsPresenter @Inject constructor(
        private val dataManager: DataManager
) : ImageSettingsContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.close()
    }
}