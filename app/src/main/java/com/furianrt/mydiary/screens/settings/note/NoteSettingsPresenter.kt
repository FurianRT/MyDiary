package com.furianrt.mydiary.screens.settings.note

import android.util.Log
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.domain.get.GetAppearanceUseCase
import com.furianrt.mydiary.domain.update.UpdateAppearanceUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class NoteSettingsPresenter @Inject constructor(
        private val updateAppearance: UpdateAppearanceUseCase,
        private val getAppearance: GetAppearanceUseCase
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
        addDisposable(getAppearance.invoke(mNoteId)
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { appearance ->
                    mAppearance = appearance
                    Log.e(TAG, "getNoteAppearance")
                    view.updateSettings(mAppearance)
                })
    }

    override fun onTextSizeChange(size: Int) {
        mAppearance.textSize = size
        updateAppearence(mAppearance)
    }

    override fun onTextColorChange(color: Int) {
        mAppearance.textColor = color
        updateAppearence(mAppearance)
    }

    override fun onSurfaceTextColorChange(color: Int) {
        mAppearance.surfaceTextColor = color
        updateAppearence(mAppearance)
    }

    override fun onBackgroundColorChange(color: Int) {
        mAppearance.background = color
        updateAppearence(mAppearance)
    }

    override fun onBackgroundTextColorChange(color: Int) {
        mAppearance.textBackground = color
        updateAppearence(mAppearance)
    }

    private fun updateAppearence(appearance: MyNoteAppearance) {
        addDisposable(updateAppearance.invoke(appearance)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }
}