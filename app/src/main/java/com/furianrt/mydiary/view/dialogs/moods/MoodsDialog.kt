/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.moods

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseDialog
import com.furianrt.mydiary.model.entity.MyMood
import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.view.dialogs.moods.MoodsDialogListAdapter.*
import kotlinx.android.synthetic.main.dialog_moods.view.*
import javax.inject.Inject

class MoodsDialog : BaseDialog(), MoodsDialogContract.MvpView,
        OnMoodListInteractionListener {

    companion object {
        const val TAG = "MoodsDialog"
        private const val ARG_NOTE_ID = "note_id"

        @JvmStatic
        fun newInstance(noteId: String) =
                MoodsDialog().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }

    @Inject
    lateinit var mPresenter: MoodsDialogContract.Presenter

    private lateinit var mAdapter: MoodsDialogListAdapter
    private lateinit var mNoteId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mNoteId = requireArguments().getString(ARG_NOTE_ID)!!
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_moods, null)

        view.button_mood_close.setOnClickListener { mPresenter.onButtonCloseClick() }

        mAdapter = MoodsDialogListAdapter(listener = this)

        val manager = LinearLayoutManager(requireContext())
        view.list_moods.layoutManager = manager
        view.list_moods.adapter = mAdapter
        view.list_moods.setHasFixedSize(true)
        view.list_moods.addItemDecoration(DividerItemDecoration(requireContext(), manager.orientation))
        return AlertDialog.Builder(requireContext())
                .setView(view)
                .create()
    }

    override fun showMoods(moods: List<MyMood>, notes: List<MyNote>) {
        mAdapter.items = moods
                .map { mood -> MoodItemView(mood, notes.count { it.moodId == mood.id }) }
                .toMutableList()
                .apply { add(MoodItemView(noteCount = notes.count { it.moodId == 0 })) }
        mAdapter.notifyDataSetChanged()
    }

    override fun onMoodClicked(mood: MyMood) {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_MOOD_CHANGED)
        mPresenter.onMoodPicked(mNoteId, mood)
    }

    override fun onNoMoodClicked() {
        mPresenter.onButtonNoMoodClick(mNoteId)
    }

    override fun closeView() {
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }
}