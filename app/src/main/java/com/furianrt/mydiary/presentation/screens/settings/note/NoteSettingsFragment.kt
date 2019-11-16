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

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat
import javax.inject.Inject

class NoteSettingsFragment : PreferenceFragmentCompat(), BaseView, NoteSettingsContract.View {

    @Inject
    lateinit var presenter: NoteSettingsContract.Presenter

    @Inject
    lateinit var analytics: MyAnalytics

    private var mListener: OnNoteSettingsFragmentListener? = null

    private val mPreferenceListener = Preference.OnPreferenceChangeListener { preference, value ->
        when {
            preference.key == TEXT_SIZE -> {
                analytics.sendEvent(MyAnalytics.EVENT_NOTE_TEXT_SIZE_CHANGED)
                presenter.onTextSizeChange((value as String).toInt())
            }
            preference.key == NOTE_TEXT_COLOR -> {
                analytics.sendEvent(MyAnalytics.EVENT_NOTE_TEXT_COLOR_CHANGED)
                presenter.onTextColorChange(value as Int)
            }
            preference.key == SURFACE_TEXT_COLOR -> {
                analytics.sendEvent(MyAnalytics.EVENT_NOTE_SURFACE_TEXT_COLOR_CHANGED)
                presenter.onSurfaceTextColorChange(value as Int)
            }
            preference.key == BACKGROUND_COLOR -> {
                analytics.sendEvent(MyAnalytics.EVENT_NOTE_BACKGROUND_CHANGED)
                presenter.onBackgroundColorChange(value as Int)
            }
            preference.key == TEXT_BACKGROUND_COLOR -> {
                analytics.sendEvent(MyAnalytics.EVENT_NOTE_TEXT_BACKGROUND_CHANGED)
                presenter.onBackgroundTextColorChange(value as Int)
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

        presenter.init(requireArguments().getString(ARG_NOTE_ID)!!)
        presenter.attachView(this)

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
        findPreference<Preference>(RESET_NOTE_SETTINGS)?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    showResetSettingsDialog()
                    return@OnPreferenceClickListener true
                }
    }

    private fun showResetSettingsDialog() {
        AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.fragment_note_settings_reset_settings_confirm))
                .setPositiveButton(R.string.reset) { dialogInterface, _ ->
                    presenter.onPrefResetSettingsClick()
                    dialogInterface.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
    }

    override fun updateSettings(appearance: MyNoteAppearance) {
        findPreference<ListPreference>(TEXT_SIZE)?.apply {
            setValueIndex(findIndexOfValue(appearance.textSize.toString()))
        }
        findPreference<ColorPreferenceCompat>(NOTE_TEXT_COLOR)?.saveValue(appearance.textColor!!)
        findPreference<ColorPreferenceCompat>(SURFACE_TEXT_COLOR)?.saveValue(appearance.surfaceTextColor!!)
        findPreference<ColorPreferenceCompat>(BACKGROUND_COLOR)?.saveValue(appearance.background!!)
        findPreference<ColorPreferenceCompat>(TEXT_BACKGROUND_COLOR)?.saveValue(appearance.textBackground!!)
    }

    override fun onAppearanceReset() {
        requireActivity().recreate()
    }

    override fun enableInput() {
        mListener?.enableInput()
    }

    override fun disableInput() {
        mListener?.disableInput()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNoteSettingsFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnNoteSettingsFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    interface OnNoteSettingsFragmentListener {
        fun enableInput()
        fun disableInput()
    }

    companion object {
        const val TAG = "NoteSettingsFragment"
        private const val ARG_NOTE_ID = "noteId"
        private const val TEXT_SIZE = "font_size_note"
        private const val NOTE_TEXT_COLOR = "note_text_color"
        private const val SURFACE_TEXT_COLOR = "surface_text_color"
        private const val TEXT_BACKGROUND_COLOR = "note_text_background"
        private const val RESET_NOTE_SETTINGS = "reset_note_settings"
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