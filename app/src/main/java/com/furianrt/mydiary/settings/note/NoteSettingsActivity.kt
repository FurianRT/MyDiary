package com.furianrt.mydiary.settings.note

import android.os.Bundle
import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseActivity
import kotlinx.android.synthetic.main.activity_note_settings.*

class NoteSettingsActivity : BaseActivity() {

    companion object {
        const val EXTRA_NOTE_ID = "noteId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_settings)

        val noteId = intent.getStringExtra(EXTRA_NOTE_ID) ?: throw IllegalArgumentException()
        addSettingsFragment(noteId)

        setSupportActionBar(toolbar_settings_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun addSettingsFragment(noteId: String) {
        if (fragmentManager.findFragmentByTag(NoteSettingsFragment.TAG) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container_settings_note, NoteSettingsFragment.newInstance(noteId),
                            NoteSettingsFragment.TAG)
                    .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}