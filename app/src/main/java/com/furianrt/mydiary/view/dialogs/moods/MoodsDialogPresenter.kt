/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.moods

import com.furianrt.mydiary.data.entity.MyMood
import com.furianrt.mydiary.domain.get.GetMoodsUseCase
import com.furianrt.mydiary.domain.update.UpdateNoteUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class MoodsDialogPresenter @Inject constructor(
        private val getMoods: GetMoodsUseCase,
        private val updateNote: UpdateNoteUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : MoodsDialogContract.Presenter() {

    override fun attachView(view: MoodsDialogContract.MvpView) {
        super.attachView(view)
        addDisposable(getMoods.invoke()
                .observeOn(scheduler.ui())
                .subscribe { moods -> view.showMoods(moods) })
    }

    override fun onButtonNoMoodClick(noteId: String) {
        addDisposable(updateNote.invoke(noteId, 0)
                .observeOn(scheduler.ui())
                .subscribe { view?.closeView() })
    }

    override fun onMoodPicked(noteId: String, mood: MyMood) {
        addDisposable(updateNote.invoke(noteId, mood.id)
                .observeOn(scheduler.ui())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCloseClick() {
        view?.closeView()
    }
}