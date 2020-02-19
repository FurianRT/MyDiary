/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseActivity
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.NoteFragment
import kotlinx.android.synthetic.main.activity_note.*
import javax.inject.Inject

class NoteActivity : BaseActivity(R.layout.activity_note), NoteActivityContract.View,
        NoteFragment.OnNoteFragmentInteractionListener {

    companion object {
        private const val EXTRA_POSITION = "position"
        private const val EXTRA_NOTE_ID = "note_id"
        private const val EXTRA_IS_NEW_NOTE = "is_new_note"

        fun newIntentModeAdd(context: Context, noteId: String) =
                Intent(context, NoteActivity::class.java).apply {
                    putExtra(EXTRA_NOTE_ID, noteId)
                    putExtra(EXTRA_IS_NEW_NOTE, true)
                }

        fun newIntentModeRead(context: Context, noteId: String, position: Int) =
                Intent(context, NoteActivity::class.java).apply {
                    putExtra(EXTRA_NOTE_ID, noteId)
                    putExtra(EXTRA_POSITION, position)
                    putExtra(EXTRA_IS_NEW_NOTE, false)
                }
    }

    @Inject
    lateinit var presenter: NoteActivityContract.Presenter

    private lateinit var mPagerAdapter: NoteActivityPagerAdapter

    private var mPagerPosition = 0
    private var mIsNewNote = true
    private var mIsEditModeEnabled = false

    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) = Unit
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
        override fun onPageSelected(position: Int) {
            mPagerPosition = position
            showImageCounter(mPagerPosition + 1, mPagerAdapter.count)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(this).inject(this)
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar_note_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        presenter.init(
                intent.getStringExtra(EXTRA_NOTE_ID)!!,
                intent.getBooleanExtra(EXTRA_IS_NEW_NOTE, true)
        )

        mPagerPosition = savedInstanceState?.getInt(EXTRA_POSITION, 0)
                ?: intent.getIntExtra(EXTRA_POSITION, 0)

        mIsNewNote = intent.getBooleanExtra(EXTRA_IS_NEW_NOTE, true)

        mPagerAdapter = NoteActivityPagerAdapter(supportFragmentManager, mIsNewNote)
        pager_note.adapter = mPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_note_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_POSITION, pager_note.currentItem)
    }

    override fun showNotes(noteIds: List<String>) {
        if (mPagerPosition >= noteIds.size) {
            mPagerPosition = noteIds.size - 1
        }
        mPagerAdapter.noteIds = noteIds
        mPagerAdapter.notifyDataSetChanged()
        pager_note.setCurrentItem(mPagerPosition, false)
        showImageCounter(mPagerPosition + 1, mPagerAdapter.count)
    }

    override fun onNoteFragmentEditModeDisabled() {
        pager_note.swipeEnabled = true
        text_toolbar_title.visibility = View.VISIBLE
        mIsEditModeEnabled = false
    }

    override fun onNoteFragmentEditModeEnabled() {
        pager_note.swipeEnabled = false
        text_toolbar_title.visibility = View.GONE
        mIsEditModeEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun savePagerPosition() {
        mPagerPosition = pager_note.currentItem
    }

    override fun closeView() {
        finish()
    }

    private fun showImageCounter(current: Int, count: Int) {
        text_toolbar_title.text = getString(R.string.counter_format, current, count)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        pager_note.addOnPageChangeListener(mOnPageChangeListener)
    }

    override fun onStop() {
        super.onStop()
        pager_note.removeOnPageChangeListener(mOnPageChangeListener)
        presenter.detachView()
    }

    override fun onNoteFragmentImagePickerOpen() {
        skipOneLock = true
    }
}
