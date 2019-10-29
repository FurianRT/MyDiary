/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.settings.note

import android.util.Log
import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.domain.reset.ResetNoteSettingsUseCase
import com.furianrt.mydiary.domain.get.GetAppearanceUseCase
import com.furianrt.mydiary.domain.update.UpdateAppearanceUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class NoteSettingsPresenter @Inject constructor(
        private val updateAppearance: UpdateAppearanceUseCase,
        private val getAppearance: GetAppearanceUseCase,
        private val resetNoteSettings: ResetNoteSettingsUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : NoteSettingsContract.Presenter() {

    companion object {
        private const val TAG = "NoteSettingsPresenter"
    }

    private lateinit var mAppearance: MyNoteAppearance
    private lateinit var mNoteId: String

    override fun init(noteId: String) {
        mNoteId = noteId
    }

    override fun attachView(view: NoteSettingsContract.MvpView) {
        super.attachView(view)
        view.disableInput()
        addDisposable(getAppearance.invoke(mNoteId)
                .firstElement()
                .observeOn(scheduler.ui())
                .subscribe { appearance ->
                    mAppearance = appearance
                    Log.e(TAG, "getNoteAppearance")
                    view.updateSettings(mAppearance)
                    view.enableInput()
                })
    }

    override fun onTextSizeChange(size: Int) {
        mAppearance.textSize = size
        updateAppearance(mAppearance)
    }

    override fun onTextColorChange(color: Int) {
        mAppearance.textColor = color
        updateAppearance(mAppearance)
    }

    override fun onSurfaceTextColorChange(color: Int) {
        mAppearance.surfaceTextColor = color
        updateAppearance(mAppearance)
    }

    override fun onBackgroundColorChange(color: Int) {
        mAppearance.background = color
        updateAppearance(mAppearance)
    }

    override fun onBackgroundTextColorChange(color: Int) {
        mAppearance.textBackground = color
        updateAppearance(mAppearance)
    }

    override fun onPrefResetSettingsClick() {
        addDisposable(resetNoteSettings.invoke(mAppearance.appearanceId)
                .observeOn(scheduler.ui())
                .subscribe { view?.onAppearanceReset() })
    }

    private fun updateAppearance(appearance: MyNoteAppearance) {
        addDisposable(updateAppearance.invoke(appearance)
                .observeOn(scheduler.ui())
                .subscribe())
    }
}