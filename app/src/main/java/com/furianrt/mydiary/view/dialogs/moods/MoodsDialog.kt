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
import com.furianrt.mydiary.data.model.MyMood
import kotlinx.android.synthetic.main.dialog_moods.view.*
import javax.inject.Inject

class MoodsDialog : BaseDialog(), MoodsDialogContract.MvpView,
        MoodsDialogListAdapter.OnMoodListInteractionListener {

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
        mNoteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalArgumentException()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_moods, null)

        view.button_mood_close.setOnClickListener { mPresenter.onButtonCloseClick() }
        view.button_no_mood.setOnClickListener { mPresenter.onButtonNoMoodClick(mNoteId) }

        mAdapter = MoodsDialogListAdapter(emptyList(), this)

        with(view.list_moods) {
            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
            adapter = mAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(requireContext(), manager.orientation))
        }

        return AlertDialog.Builder(requireContext())
                .setView(view)
                .create()
    }

    override fun showMoods(moods: List<MyMood>) {
        mAdapter.moods = moods
        mAdapter.notifyDataSetChanged()
    }

    override fun onMoodClicked(mood: MyMood) {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_MOOD_CHANGED)
        mPresenter.onMoodPicked(mNoteId, mood)
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