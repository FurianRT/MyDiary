/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.settings.note

import android.os.Bundle
import android.view.View
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseActivity
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.activity_note_settings.*

class NoteSettingsActivity : BaseActivity(R.layout.activity_note_settings),
        NoteSettingsFragment.OnNoteSettingsFragmentListener {

    companion object {
        const val EXTRA_NOTE_ID = "noteId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID) ?: throw IllegalArgumentException()
        addSettingsFragment(noteId)

        setSupportActionBar(toolbar_settings_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        view_input_lock.setOnTouchListener { _, _ -> true }
    }

    private fun addSettingsFragment(noteId: String) {
        if (supportFragmentManager.findFragmentByTag(NoteSettingsFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                add(R.id.container_settings_note, NoteSettingsFragment.newInstance(noteId), NoteSettingsFragment.TAG)
            }
        }
    }

    override fun enableInput() {
        view_input_lock.visibility = View.GONE
    }

    override fun disableInput() {
        view_input_lock.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}