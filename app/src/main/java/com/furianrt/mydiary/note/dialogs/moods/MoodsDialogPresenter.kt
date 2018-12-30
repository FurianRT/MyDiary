package com.furianrt.mydiary.note.dialogs.moods

import com.furianrt.mydiary.data.DataManager

class MoodsDialogPresenter(private val mDataManager: DataManager) : MoodsDialogContract.Presenter {

    private var mView: MoodsDialogContract.View? = null

    override fun attachView(view: MoodsDialogContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }

    override fun onViewCreate() {
        addDisposable( mDataManager.getAllMoods()
                .subscribe { moods -> mView?.showMoods(moods) })
    }
}