package com.furianrt.mydiary.screens.settings.note

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNoteAppearance
import io.reactivex.android.schedulers.AndroidSchedulers

class NoteSettingsPresenter(
        private val mDataManager: DataManager
) : NoteSettingsContract.Presenter() {

    companion object {
        private const val TAG = "NoteSettingsPresenter"
    }

    private lateinit var mAppearance: MyNoteAppearance

    override fun onViewCreate(noteId: String?) {
        addDisposable(mDataManager.getNoteAppearance(noteId ?: throw IllegalArgumentException())
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { appearance ->
                    mAppearance = appearance
                    mAppearance.textSize = mAppearance.textSize ?: mDataManager.getTextSize()
                    mAppearance.textColor = mAppearance.textColor ?: mDataManager.getTextColor()
                    mAppearance.background =
                            mAppearance.background ?: mDataManager.getNoteBackgroundColor()
                    mAppearance.textBackground =
                            mAppearance.textBackground ?: mDataManager.getNoteTextBackgroundColor()
                    Log.e(TAG, "getNoteAppearance")
                    view?.updateSettings(mAppearance)
                })
    }

    override fun onTextSizeChange(size: Int) {
        mAppearance.textSize = size
        addDisposable(mDataManager.updateAppearance(mAppearance)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onTextColorChange(color: Int) {
        mAppearance.textColor = color
        addDisposable(mDataManager.updateAppearance(mAppearance)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onBackgroundColorChange(color: Int) {
        mAppearance.background = color
        addDisposable(mDataManager.updateAppearance(mAppearance)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onBackgroundTextColorChange(color: Int) {
        mAppearance.textBackground = color
        addDisposable(mDataManager.updateAppearance(mAppearance)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }
}