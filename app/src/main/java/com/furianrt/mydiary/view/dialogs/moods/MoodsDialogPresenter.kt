package com.furianrt.mydiary.view.dialogs.moods

import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.domain.get.GetMoodsUseCase
import com.furianrt.mydiary.domain.update.UpdateNoteUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class MoodsDialogPresenter @Inject constructor(
        private val getMoods: GetMoodsUseCase,
        private val updateNote: UpdateNoteUseCase
) : MoodsDialogContract.Presenter() {

    override fun attachView(view: MoodsDialogContract.MvpView) {
        super.attachView(view)
        addDisposable(getMoods.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { moods -> view.showMoods(moods) })
    }

    override fun onButtonNoMoodClick(noteId: String) {
        addDisposable(updateNote.invoke(noteId, 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onMoodPicked(noteId: String, mood: MyMood) {
        addDisposable(updateNote.invoke(noteId, mood.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCloseClick() {
        view?.closeView()
    }
}