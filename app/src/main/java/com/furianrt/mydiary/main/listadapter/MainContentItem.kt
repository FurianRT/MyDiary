package com.furianrt.mydiary.main.listadapter

import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteWithProp

class MainContentItem(val note: MyNoteWithProp) : MainListItem() {

    override fun getType(): Int = R.layout.activity_main_list_content
}