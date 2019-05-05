package com.furianrt.mydiary.dialogs.delete.image

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.android.schedulers.AndroidSchedulers

class DeleteImagePresenter(
        private val dataManager: DataManager
) : DeleteImageContract.Presenter() {

    override fun onButtonDeleteClick(images: List<MyImage>) {
        addDisposable(dataManager.deleteImage(images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}