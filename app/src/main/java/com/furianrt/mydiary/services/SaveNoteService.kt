/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterContextModule
import com.furianrt.mydiary.domain.update.UpdateNoteSpansUseCase
import com.furianrt.mydiary.domain.update.UpdateNoteUseCase
import com.furianrt.mydiary.model.entity.MyTextSpan
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SaveNoteService : Service() {

    companion object {
        private const val EXTRA_NOTE_ID = "note_id"
        private const val EXTRA_NOTE_TITLE = "note_title"
        private const val EXTRA_NOTE_CONTENT = "note_content"
        private const val EXTRA_NOTE_SPANS = "note_spans"

        fun getIntent(
                context: Context,
                noteId: String,
                noteTitle: String,
                noteContent: String,
                noteSpans: List<MyTextSpan>
        ) = Intent(context, SaveNoteService::class.java).apply {
            putExtra(EXTRA_NOTE_ID, noteId)
            putExtra(EXTRA_NOTE_TITLE, noteTitle)
            putExtra(EXTRA_NOTE_CONTENT, noteContent)
            putParcelableArrayListExtra(EXTRA_NOTE_SPANS, ArrayList(noteSpans))
        }
    }

    @Inject
    lateinit var updateNoteSpansUseCase: UpdateNoteSpansUseCase

    @Inject
    lateinit var updateNote: UpdateNoteUseCase

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate() {
        (applicationContext as MyApp)
                .component
                .newPresenterComponent(PresenterContextModule(baseContext))
                .inject(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { saveNoteData(it) }
        return START_REDELIVER_INTENT
    }

    private fun saveNoteData(intent: Intent) {
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID)
        if (noteId == null) {
            stopSelf()
            return
        }
        val title = intent.getStringExtra(EXTRA_NOTE_TITLE) ?: ""
        val content = intent.getStringExtra(EXTRA_NOTE_CONTENT) ?: ""
        val spans: List<MyTextSpan> = intent.getParcelableArrayListExtra(EXTRA_NOTE_SPANS)
                ?: emptyList()

        mCompositeDisposable.add(updateNote(noteId, title, content)
                .andThen(updateNoteSpansUseCase.invoke(noteId, spans))
                .subscribe { stopSelf() })
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    override fun onBind(intent: Intent): IBinder? = null
}
