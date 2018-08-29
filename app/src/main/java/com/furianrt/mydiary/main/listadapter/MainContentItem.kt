package com.furianrt.mydiary.main.listadapter

import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote

class MainContentItem(val note: MyNote) : MainListItem() {

    override fun getType(): Int = R.layout.activity_main_list_content
}