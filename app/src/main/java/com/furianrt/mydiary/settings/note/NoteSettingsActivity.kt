package com.furianrt.mydiary.settings.note

import android.os.Bundle
import android.preference.PreferenceManager
import com.furianrt.mydiary.BaseActivity
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import kotlinx.android.synthetic.main.activity_note_settings.*

class NoteSettingsActivity : BaseActivity() {

    companion object {
        const val EXTRA_NOTE = "note"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_settings)

        val note = intent.getParcelableExtra<MyNote>(EXTRA_NOTE) ?: throw IllegalArgumentException()
        addSettingsFragment(note)

        setSupportActionBar(toolbar_settings_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        PreferenceManager.setDefaultValues(this, R.xml.pref_note, false)
    }

    private fun addSettingsFragment(note: MyNote) {
        if (fragmentManager.findFragmentByTag(NoteSettingsFragment.TAG) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container_settings_note, NoteSettingsFragment.newInstance(note),
                            NoteSettingsFragment.TAG)
                    .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}