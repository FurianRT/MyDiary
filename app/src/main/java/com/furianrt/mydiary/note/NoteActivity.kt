package com.furianrt.mydiary.note

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import kotlinx.android.synthetic.main.activity_note.*
import javax.inject.Inject

const val EXTRA_POSITION = "position"
const val EXTRA_MODE = "mode"

enum class Mode { ADD, READ }

class NoteActivity : AppCompatActivity(), NoteActivityContract.View {

    @Inject
    lateinit var mPresenter: NoteActivityContract.Presenter

    private var mPagerPosition = 0

    private lateinit var mPagerAdapter: NotePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_note)

        mPresenter.attachView(this)

        mPagerPosition = savedInstanceState?.getInt(EXTRA_POSITION, 0) ?:
                intent.getIntExtra(EXTRA_POSITION, 0)

        val mode = intent.getSerializableExtra(EXTRA_MODE) as Mode

        mPagerAdapter = NotePagerAdapter(supportFragmentManager, mode)

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

        mPresenter.loadNotes(mode)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(EXTRA_POSITION, pager_note.currentItem)
    }

    override fun showNotes(notes: List<MyNote>) {
        mPagerAdapter.list = notes
        Log.e(LOG_TAG, "notify")
        mPagerAdapter.notifyDataSetChanged()
        pager_note.setCurrentItem(mPagerPosition, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    fun savePagerPosition() {
        mPagerPosition = pager_note.currentItem
    }
}
