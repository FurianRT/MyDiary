package com.furianrt.mydiary.note.dialogs.moods

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyMood
import kotlinx.android.synthetic.main.dialog_moods_list_item.view.*

class MoodsDialogListAdapter(
        var moods: List<MyMood>,
        var listener: OnMoodListInteractionListener?
) : RecyclerView.Adapter<MoodsDialogListAdapter.MoodsListViewHolder>() {

    override fun getItemCount(): Int = moods.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodsListViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dialog_moods_list_item, parent, false)
        return MoodsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodsListViewHolder, position: Int) {
        holder.bind(moods[position])
    }

    inner class MoodsListViewHolder(
            private val mView: View
    ) : RecyclerView.ViewHolder(mView), View.OnClickListener {

        private lateinit var mMood: MyMood

        fun bind(mood: MyMood) {
            mView.setOnClickListener(this)
            mView.text_item_mood.text = mood.name
            mMood = mood
            val smile = mView.context
                    .resources
                    .getIdentifier(mood.iconName, "drawable", mView.context.packageName)
            mView.text_item_mood.setCompoundDrawablesWithIntrinsicBounds(smile, 0, 0, 0)
        }

        override fun onClick(v: View?) {
            listener?.onMoodClicked(mMood)
        }
    }

    interface OnMoodListInteractionListener {

        fun onMoodClicked(mood: MyMood)
    }
}