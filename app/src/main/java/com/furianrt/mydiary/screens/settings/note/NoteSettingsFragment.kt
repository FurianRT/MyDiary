package com.furianrt.mydiary.screens.settings.note

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.general.Analytics
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat
import javax.inject.Inject

class NoteSettingsFragment : PreferenceFragmentCompat(), NoteSettingsContract.View {

    @Inject
    lateinit var mPresenter: NoteSettingsContract.Presenter

    private val mPreferenceListener = Preference.OnPreferenceChangeListener { preference, value ->
        when {
            preference.key == TEXT_SIZE -> {
                Analytics.sendEvent(requireContext(), Analytics.EVENT_NOTE_TEXT_SIZE_CHANGED)
                mPresenter.onTextSizeChange((value as String).toInt())
            }
            preference.key == NOTE_TEXT_COLOR -> {
                Analytics.sendEvent(requireContext(), Analytics.EVENT_NOTE_TEXT_COLOR_CHANGED)
                mPresenter.onTextColorChange(value as Int)
            }
            preference.key == SURFACE_TEXT_COLOR -> {
                Analytics.sendEvent(requireContext(), Analytics.EVENT_NOTE_SURFACE_TEXT_COLOR_CHANGED)
                mPresenter.onSurfaceTextColorChange(value as Int)
            }
            preference.key == BACKGROUND_COLOR -> {
                Analytics.sendEvent(requireContext(), Analytics.EVENT_NOTE_BACKGROUD_CHANGED)
                mPresenter.onBackgroundColorChange(value as Int)
            }
            preference.key == TEXT_BACKGROUND_COLOR -> {
                Analytics.sendEvent(requireContext(), Analytics.EVENT_NOTE_TEXT_BACKGROUD_CHANGED)
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

        findPreference<ColorPreferenceCompat>(NOTE_TEXT_COLOR)?.let {
            it.isPersistent = false
            it.onPreferenceChangeListener = mPreferenceListener
        }

        findPreference<ColorPreferenceCompat>(SURFACE_TEXT_COLOR)?.let {
            it.isPersistent = false
            it.onPreferenceChangeListener = mPreferenceListener
        }

        findPreference<ColorPreferenceCompat>(BACKGROUND_COLOR)?.let {
            it.isPersistent = false
            it.onPreferenceChangeListener = mPreferenceListener
        }

        findPreference<ColorPreferenceCompat>(TEXT_BACKGROUND_COLOR)?.let {
            it.isPersistent = false
            it.onPreferenceChangeListener = mPreferenceListener
        }
    }

    override fun updateSettings(appearance: MyNoteAppearance) {
        findPreference<ListPreference>(TEXT_SIZE)?.apply {
            setValueIndex(this.findIndexOfValue(appearance.textSize.toString()))
        }
        findPreference<ColorPreferenceCompat>(NOTE_TEXT_COLOR)?.saveValue(appearance.textColor!!)
        findPreference<ColorPreferenceCompat>(SURFACE_TEXT_COLOR)?.saveValue(appearance.surfaceTextColor!!)
        findPreference<ColorPreferenceCompat>(BACKGROUND_COLOR)?.saveValue(appearance.background!!)
        findPreference<ColorPreferenceCompat>(TEXT_BACKGROUND_COLOR)?.saveValue(appearance.textBackground!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    companion object {

        const val TAG = "NoteSettingsFragment"

        private const val ARG_NOTE_ID = "noteId"
        private const val TEXT_SIZE = "font_size_note"
        private const val NOTE_TEXT_COLOR = "note_text_color"
        private const val SURFACE_TEXT_COLOR = "surface_text_color"
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