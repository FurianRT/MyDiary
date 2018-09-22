package com.furianrt.mydiary.note

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.main.EXTRA_CLICKED_NOTE_POSITION
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import kotlinx.android.synthetic.main.activity_note.*
import javax.inject.Inject

const val EXTRA_MODE = "mode"

enum class Mode { ADD, READ }

class NoteActivity : AppCompatActivity(), NoteActivityContract.View,
        NoteEditFragment.OnNoteFragmentInteractionListener {

    @Inject
    lateinit var mPresenter: NoteActivityContract.Presenter

    private var mPagerPosition = 0

    private lateinit var mPagerAdapter: NoteActivityPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_note)

        mPresenter.attachView(this)

        mPagerPosition = savedInstanceState?.getInt(EXTRA_CLICKED_NOTE_POSITION, 0)
                ?: intent.getIntExtra(EXTRA_CLICKED_NOTE_POSITION, 0)

        val mode = intent.getSerializableExtra(EXTRA_MODE) as Mode

        mPagerAdapter = NoteActivityPagerAdapter(supportFragmentManager, mode)

        setupUi()

        mPresenter.loadNotes(mode)
    }

    private fun setupUi() {
        setSupportActionBar(toolbar_note_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        pager_note.adapter = mPagerAdapter
        pager_note.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(position: Int) {
                mPagerPosition = position
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_note_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(EXTRA_CLICKED_NOTE_POSITION, pager_note.currentItem)
    }

    override fun showNotes(notes: List<MyNoteWithProp>) {
        mPagerAdapter.list = notes
        Log.e(LOG_TAG, "notify")
        mPagerAdapter.notifyDataSetChanged()
        pager_note.setCurrentItem(mPagerPosition, false)
    }

    override fun onNoteFragmentEditModeDisabled() {
        pager_note.swipeEnabled = true
    }

    override fun onNoteFragmentEditModeEnabled() {
        pager_note.swipeEnabled = false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    fun savePagerPosition() {
        mPagerPosition = pager_note.currentItem
    }
}
