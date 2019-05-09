package com.furianrt.mydiary.screens.settings.note

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat
import javax.inject.Inject

class NoteSettingsFragment : PreferenceFragmentCompat(), NoteSettingsContract.View {

    @Inject
    lateinit var mPresenter: NoteSettingsContract.Presenter

    private val mPreferenceListener = Preference.OnPreferenceChangeListener { preference, value ->
        when {
            preference.key == TEXT_SIZE -> mPresenter.onTextSizeChange((value as String).toInt())
            preference.key == TEXT_COLOR -> {
                (preference as ColorPreferenceCompat).setDefaultValue(value)
                mPresenter.onTextColorChange(value as Int)
            }
            preference.key == BACKGROUND_COLOR -> {
                (preference as ColorPreferenceCompat).setDefaultValue(value)
                mPresenter.onBackgroundColorChange(value as Int)
            }
            preference.key == TEXT_BACKGROUND_COLOR -> {
                (preference as ColorPreferenceCompat).setDefaultValue(value)
                mPresenter.onBackgroundTextColorChange(value as Int)
            }
        }
        return@OnPreferenceChangeListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireActivity()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_note, rootKey)

        mPresenter.attachView(this)
        mPresenter.onViewCreate(arguments?.getString(ARG_NOTE_ID))

        findPreference<ListPreference>(TEXT_SIZE)?.onPreferenceChangeListener = mPreferenceListener
        findPreference<ColorPreferenceCompat>(TEXT_COLOR)?.onPreferenceChangeListener = mPreferenceListener
        findPreference<ColorPreferenceCompat>(BACKGROUND_COLOR)?.onPreferenceChangeListener = mPreferenceListener
        findPreference<ColorPreferenceCompat>(TEXT_BACKGROUND_COLOR)?.onPreferenceChangeListener = mPreferenceListener
    }

    override fun updateSettings(appearance: MyNoteAppearance) {
        findPreference<ListPreference>(TEXT_SIZE)?.apply {
            setValueIndex(this.findIndexOfValue(appearance.textSize.toString()))
        }
        findPreference<ColorPreferenceCompat>(TEXT_COLOR)?.setDefaultValue(appearance.textColor)
        findPreference<ColorPreferenceCompat>(BACKGROUND_COLOR)?.setDefaultValue(appearance.background)
        findPreference<ColorPreferenceCompat>(TEXT_BACKGROUND_COLOR)?.setDefaultValue(appearance.textBackground)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    companion object {

        const val TAG = "NoteSettingsFragment"

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