package com.furianrt.mydiary.screens.settings.note

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNoteAppearance
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class NoteSettingsPresenter @Inject constructor(
        private val dataManager: DataManager
) : NoteSettingsContract.Presenter() {

    companion object {
        private const val TAG = "NoteSettingsPresenter"
    }

    private lateinit var mAppearance: MyNoteAppearance

    override fun onViewCreate(noteId: String?) {
        addDisposable(dataManager.getNoteAppearance(noteId ?: throw IllegalArgumentException())
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { appearance ->
                    mAppearance = appearance
                    mAppearance.textSize = mAppearance.textSize ?: dataManager.getTextSize()
                    mAppearance.textColor = mAppearance.textColor ?: dataManager.getTextColor()
                    mAppearance.surfaceTextColor =
                            mAppearance.surfaceTextColor ?: dataManager.getSurfaceTextColor()
                    mAppearance.background =
                            mAppearance.background ?: dataManager.getNoteBackgroundColor()
                    mAppearance.textBackground =
                            mAppearance.textBackground ?: dataManager.getNoteTextBackgroundColor()
                    Log.e(TAG, "getNoteAppearance")
                    view?.updateSettings(mAppearance)
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
        addDisposable(dataManager.updateAppearance(appearance)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }
}