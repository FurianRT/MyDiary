package com.furianrt.mydiary.data.model.pojo

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.model.MyTag

class SearchEntries(
        val tags: List<MyTag>,
        val categories: List<MyCategory>,
        val location: List<MyLocation>,
        val moods: List<MyMood>
)