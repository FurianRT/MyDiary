package com.furianrt.mydiary.note.dialogs.moods

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyMood
import kotlinx.android.synthetic.main.dialog_moods.view.*
import javax.inject.Inject

class MoodsDialog : DialogFragment(), MoodsDialogContract.View,
        MoodsDialogListAdapter.OnMoodListInteractionListener {

    @Inject
    lateinit var mPresenter: MoodsDialogContract.Presenter

    private var mListener: OnMoodsDialogInteractionListener? = null
    private lateinit var mAdapter: MoodsDialogListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(context!!).inject(this)
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        mPresenter.attachView(this)

        val view = activity?.layoutInflater?.inflate(R.layout.dialog_moods, null)

        mAdapter = MoodsDialogListAdapter(emptyList(), this)

        view?.list_moods?.apply {
            val manager = LinearLayoutManager(context)
            layoutManager = manager
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(context, manager.orientation))
        }

        mPresenter.onViewCreate()

        return AlertDialog.Builder(context!!)
                .setView(view)
                .setPositiveButton(getString(R.string.close), null)
                .setNegativeButton(getString(R.string.no_mood)) { _, _ ->
                    mListener?.onNoMoodPicked()
                }
                .create()
    }

    override fun showMoods(moods: List<MyMood>) {
        mAdapter.moods = moods
        mAdapter.notifyDataSetChanged()
    }

    fun setOnMoodsDialogInteractionListener(listener: OnMoodsDialogInteractionListener?) {
        mListener = listener
    }

    override fun onMoodClicked(mood: MyMood) {
        mListener?.onMoodPicked(mood)
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        mListener = null
        mPresenter.detachView()
    }

    interface OnMoodsDialogInteractionListener {

        fun onMoodPicked(mood: MyMood)

        fun onNoMoodPicked()
    }

    companion object {
        const val TAG = "MoodsDialog"
    }
}