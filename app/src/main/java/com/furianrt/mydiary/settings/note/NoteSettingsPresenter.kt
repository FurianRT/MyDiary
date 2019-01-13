package com.furianrt.mydiary.settings.note

import android.util.Log
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNoteAppearance

class NoteSettingsPresenter(
        private val mDataManager: DataManager
) : NoteSettingsContract.Presenter() {

    private var mView: NoteSettingsContract.View? = null

    private lateinit var mAppearance: MyNoteAppearance

    override fun onViewCreate(noteId: String?) {
        addDisposable(mDataManager.getNoteAppearance(noteId ?: throw IllegalArgumentException())
                .firstElement()
                .subscribe { appearance ->
                    mAppearance = appearance
                    mAppearance.textSize = mAppearance.textSize ?: mDataManager.getTextSize()
                    mAppearance.textColor = mAppearance.textColor ?: mDataManager.getTextColor()
                    mAppearance.background =
                            mAppearance.background ?: mDataManager.getNoteBackgroundColor()
                    mAppearance.textBackground =
                            mAppearance.textBackground ?: mDataManager.getNoteTextBackgroundColor()
                    Log.e(LOG_TAG, "getNoteAppearance")
                    mView?.updateSettings(mAppearance)
                })
    }

    override fun onTextSizeChange(size: Int) {
        mAppearance.textSize = size
        addDisposable(mDataManager.updateAppearance(mAppearance)
                .subscribe())
    }

    override fun onTextColorChange(color: Int) {
        mAppearance.textColor = color
        addDisposable(mDataManager.updateAppearance(mAppearance)
                .subscribe())
    }

    override fun onBackgroundColorChange(color: Int) {
        mAppearance.background = color
        addDisposable(mDataManager.updateAppearance(mAppearance)
                .subscribe())
    }

    override fun onBackgroundTextColorChange(color: Int) {
        mAppearance.textBackground = color
        addDisposable(mDataManager.updateAppearance(mAppearance)
                .subscribe())
    }

    override fun attachView(view: NoteSettingsContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }
}