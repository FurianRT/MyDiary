package com.furianrt.mydiary.settings.note

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.rarepebble.colorpicker.ColorPreference
import javax.inject.Inject

class NoteSettingsFragment : PreferenceFragment(), NoteSettingsContract.View {

    @Inject
    lateinit var mPresenter: NoteSettingsContract.Presenter

    private val mPreferenceListener = Preference.OnPreferenceChangeListener { preference, value ->
        when {
            preference.key == TEXT_SIZE -> mPresenter.onTextSizeChange((value as String).toInt())
            preference.key == TEXT_COLOR -> {
                (preference as ColorPreference).setDefaultValue(value)
                mPresenter.onTextColorChange(value as Int)
            }
            preference.key == BACKGROUND_COLOR -> {
                (preference as ColorPreference).setDefaultValue(value)
                mPresenter.onBackgroundColorChange(value as Int)
            }
            preference.key == TEXT_BACKGROUND_COLOR -> {
                (preference as ColorPreference).setDefaultValue(value)
                mPresenter.onBackgroundTextColorChange(value as Int)
            }
        }
        return@OnPreferenceChangeListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_note)

        getPresenterComponent(activity).inject(this)

        mPresenter.attachView(this)
        mPresenter.onViewCreate(arguments?.getString(ARG_NOTE_ID))

        (findPreference(TEXT_SIZE) as ListPreference).onPreferenceChangeListener = mPreferenceListener
        (findPreference(TEXT_COLOR) as ColorPreference).onPreferenceChangeListener = mPreferenceListener
        (findPreference(BACKGROUND_COLOR) as ColorPreference).onPreferenceChangeListener = mPreferenceListener
        (findPreference(TEXT_BACKGROUND_COLOR) as ColorPreference).onPreferenceChangeListener = mPreferenceListener
    }

    override fun updateSettings(appearance: MyNoteAppearance) {
        (findPreference(TEXT_SIZE) as ListPreference).apply {
            setValueIndex(this.findIndexOfValue(appearance.textSize.toString()))
        }
        (findPreference(TEXT_COLOR) as ColorPreference).setDefaultValue(appearance.textColor)
        (findPreference(BACKGROUND_COLOR) as ColorPreference).setDefaultValue(appearance.background)
        (findPreference(TEXT_BACKGROUND_COLOR) as ColorPreference).setDefaultValue(appearance.textBackground)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    companion object {

        val TAG = NoteSettingsFragment::class.toString()

        private const val ARG_NOTE_ID = "noteId"
        private const val TEXT_SIZE = "font_size_note"
        private const val TEXT_COLOR = "note_text_color"
        private const val TEXT_BACKGROUND_COLOR = "note_text_background"
        private const val BACKGROUND_COLOR = "note_background"

        @JvmStatic
        fun newInstance(noteId: String) =
                NoteSettingsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }
}