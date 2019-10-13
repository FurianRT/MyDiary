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
import com.furianrt.mydiary.data.entity.MyNote
import com.furianrt.mydiary.domain.get.GetMoodsUseCase
import com.furianrt.mydiary.domain.get.GetNotesUseCase
import com.furianrt.mydiary.domain.update.UpdateNoteUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class MoodsDialogPresenter @Inject constructor(
        private val getMoods: GetMoodsUseCase,
        private val updateNote: UpdateNoteUseCase,
        private val getNotes: GetNotesUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : MoodsDialogContract.Presenter() {

    private class MoodsAndNotes(val moods: List<MyMood>, val notes: List<MyNote>)

    override fun attachView(view: MoodsDialogContract.MvpView) {
        super.attachView(view)
        addDisposable(Flowable.combineLatest(
                getMoods.invoke(),
                getNotes.invoke().firstOrError().toFlowable(),
                BiFunction<List<MyMood>, List<MyNote>, MoodsAndNotes> { moods, notes ->
                    MoodsAndNotes(moods, notes)
                }
        )
                .observeOn(scheduler.ui())
                .subscribe { view.showMoods(it.moods, it.notes) })
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