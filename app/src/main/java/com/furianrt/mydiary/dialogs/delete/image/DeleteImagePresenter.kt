package com.furianrt.mydiary.dialogs.delete.image

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class DeleteImagePresenter(
        private val dataManager: DataManager
) : DeleteImageContract.Presenter() {

    override fun onButtonDeleteClick(imageNames: List<String>) {
        addDisposable(dataManager.deleteImage(imageNames)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}