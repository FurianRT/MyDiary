/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note.fragments.mainnote

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.furianrt.mydiary.R
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import com.furianrt.mydiary.utils.dpToPx
import kotlinx.android.synthetic.main.spinner_color_item.view.*

class ColorSpinnerAdapter(
        context: Context,
        colorsRes: Int
) : ArrayAdapter<String>(context, R.layout.spinner_color_item, context.resources.getStringArray(colorsRes)) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
            getView(position, convertView, parent)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewHolder = ViewHolder()
        val rowView = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.spinner_color_item, parent, false).apply {
                viewHolder.color = view_color
                tag = viewHolder
            }
        } else {
            viewHolder = convertView.tag as ViewHolder
            convertView
        }

        return rowView.apply {
            viewHolder.color?.setBackgroundColor(Color.parseColor(getItem(position)))

            if (position == 0) {
                val params = view_color.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = dpToPx(4f)
            }
        }
    }

    inner class ViewHolder(
            var color: View? = null
    )
}