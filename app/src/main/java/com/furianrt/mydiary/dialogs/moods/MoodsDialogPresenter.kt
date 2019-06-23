package com.furianrt.mydiary.dialogs.moods

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyMood
import io.reactivex.android.schedulers.AndroidSchedulers

class MoodsDialogPresenter(
        private val dataManager: DataManager
) : MoodsDialogContract.Presenter() {

    override fun attachView(view: MoodsDialogContract.MvpView) {
        super.attachView(view)
        addDisposable(dataManager.getAllMoods()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { moods -> view.showMoods(moods) })
    }

    override fun onButtonNoMoodClick(noteId: String) {
        addDisposable(dataManager.getNote(noteId)
                .firstOrError()
                .flatMapCompletable { dataManager.updateNote(it.apply { moodId = 0 }) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onMoodPicked(noteId: String, mood: MyMood) {
        addDisposable(dataManager.getNote(noteId)
                .firstOrError()
                .flatMapCompletable { dataManager.updateNote(it.apply { moodId = mood.id }) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCloseClick() {
        view?.closeView()
    }
}