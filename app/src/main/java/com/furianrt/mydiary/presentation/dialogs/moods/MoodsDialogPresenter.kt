/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.moods

import com.furianrt.mydiary.model.entity.MyMood
import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.domain.get.GetMoodsUseCase
import com.furianrt.mydiary.domain.get.GetNotesUseCase
import com.furianrt.mydiary.domain.update.UpdateNoteUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class MoodsDialogPresenter @Inject constructor(
        private val getMoodsUseCase: GetMoodsUseCase,
        private val updateNoteUseCase: UpdateNoteUseCase,
        private val getNotesUseCase: GetNotesUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : MoodsDialogContract.Presenter() {

    private class MoodsAndNotes(val moods: List<MyMood>, val notes: List<MyNote>)

    override fun attachView(view: MoodsDialogContract.View) {
        super.attachView(view)
        addDisposable(Flowable.combineLatest(
                getMoodsUseCase(),
                getNotesUseCase().firstOrError().toFlowable(),
                { moods, notes -> MoodsAndNotes(moods, notes) }
        )
                .observeOn(scheduler.ui())
                .subscribe { view.showMoods(it.moods, it.notes) })
    }

    override fun onButtonNoMoodClick(noteId: String) {
        addDisposable(updateNoteUseCase(noteId, 0)
                .observeOn(scheduler.ui())
                .subscribe { view?.closeView() })
    }

    override fun onMoodPicked(noteId: String, mood: MyMood) {
        addDisposable(updateNoteUseCase(noteId, mood.id)
                .observeOn(scheduler.ui())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCloseClick() {
        view?.closeView()
    }
}