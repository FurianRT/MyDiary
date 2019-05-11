package com.furianrt.mydiary.screens.settings.note

import android.os.Bundle
import android.view.View
import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
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

        view_ad.loadAd(AdRequest.Builder().build())
        view_ad.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                view_ad?.visibility = View.VISIBLE
            }
        }
    }

    private fun addSettingsFragment(noteId: String) {
        if (supportFragmentManager.findFragmentByTag(NoteSettingsFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
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