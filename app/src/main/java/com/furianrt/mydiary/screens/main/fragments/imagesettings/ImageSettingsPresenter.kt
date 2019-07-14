package com.furianrt.mydiary.screens.main.fragments.imagesettings

import javax.inject.Inject

class ImageSettingsPresenter @Inject constructor() : ImageSettingsContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.close()
    }
}