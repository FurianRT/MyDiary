package com.furianrt.mydiary.settings.note

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote

private const val ARG_NOTE = "note"

class NoteSettingsFragment : PreferenceFragment() {

    private lateinit var mNote: MyNote

    private val mPreferenceListener = Preference.OnPreferenceChangeListener { preference, value ->
        Log.e("ttt", "mPreferenceListener")
        if (preference.key == "test_test") {

        }
        return@OnPreferenceChangeListener false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_note)

        mNote = arguments?.getParcelable(ARG_NOTE) ?: throw IllegalArgumentException()

        //(findPreference("test_test") as ListPreference?)?.setValueIndex(note.moodId)

    }

    companion object {

        val TAG = NoteSettingsFragment::class.toString()

        @JvmStatic
        fun newInstance(note: MyNote) =
                NoteSettingsFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_NOTE, note)
                    }
                }
    }
}