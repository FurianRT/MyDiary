/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.settings.global

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.preference.ListPreference
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import kotlinx.android.synthetic.main.dialog_font.view.*
import kotlinx.android.synthetic.main.dialog_font_item.view.*

class FontListPreference : ListPreference {

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    @SuppressLint("InflateParams")
    override fun onClick() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_font, null)

        val alertDialog = AlertDialog.Builder(context)
                .setView(view)
                .create()

        view.list_font.adapter = FontAdapter { position ->
            if (callChangeListener(entryValues[position].toString())) {
                setValueIndex(position)
            }
            alertDialog.dismiss()
        }
        view.list_font.layoutManager = LinearLayoutManager(context)
        view.text_font_title.text = context.getString(R.string.all_notes_text_style_settings_title)

        view.button_font_close.setOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }

    private inner class FontAdapter(
            private val callback: (position: Int) -> Unit
    ) : RecyclerView.Adapter<FontAdapter.FontViewHolder>() {

        override fun getItemCount(): Int = entries?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontViewHolder =
                FontViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.dialog_font_item, parent, false))

        override fun onBindViewHolder(holder: FontViewHolder, position: Int) {
            entries?.let { holder.bind(it[position]) }
        }

        private inner class FontViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            fun bind(item: CharSequence) {
                itemView.setOnClickListener { callback.invoke(adapterPosition) }
                if (adapterPosition != RecyclerView.NO_POSITION && !entryValues.isNullOrEmpty()) {
                    itemView.button_font.isChecked = value == entryValues[adapterPosition].toString()
                }
                itemView.text_font_name.text = item
                itemView.text_font_name.typeface = when (adapterPosition) {
                    1 -> ResourcesCompat.getFont(itemView.context, R.font.arima_madurai)
                    2 -> ResourcesCompat.getFont(itemView.context, R.font.arimo_regular)
                    3 -> ResourcesCompat.getFont(itemView.context, R.font.bad_script)
                    4 -> ResourcesCompat.getFont(itemView.context, R.font.caveat)
                    5 -> ResourcesCompat.getFont(itemView.context, R.font.cuprum_regular)
                    6 -> ResourcesCompat.getFont(itemView.context, R.font.gabriela)
                    7 -> ResourcesCompat.getFont(itemView.context, R.font.ibm_plex_mono)
                    8 -> ResourcesCompat.getFont(itemView.context, R.font.noto_serif)
                    9 -> ResourcesCompat.getFont(itemView.context, R.font.philosopher_regular)
                    10 -> ResourcesCompat.getFont(itemView.context, R.font.play_regular)
                    11 -> ResourcesCompat.getFont(itemView.context, R.font.poppins_light)
                    12 -> ResourcesCompat.getFont(itemView.context, R.font.product_sans)
                    13 -> ResourcesCompat.getFont(itemView.context, R.font.roboto)
                    14 -> ResourcesCompat.getFont(itemView.context, R.font.roboto_slab)
                    15 -> ResourcesCompat.getFont(itemView.context, R.font.robotomedium)
                    16 -> ResourcesCompat.getFont(itemView.context, R.font.ubuntu)
                    else -> Typeface.create("sans-serif", Typeface.NORMAL)
                }
            }
        }
    }
}