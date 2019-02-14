package com.furianrt.mydiary.note.dialogs.moods

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class MoodsDialogPresenter(private val mDataManager: DataManager) : MoodsDialogContract.Presenter() {

    override fun onViewCreate() {
        addDisposable( mDataManager.getAllMoods()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { moods -> view?.showMoods(moods) })
    }
}