package com.furianrt.mydiary.data.model.pojo

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.model.MyTag

class SearchEntries(
        var tags: List<MyTag>,
        var categories: List<MyCategory>,
        var locations: List<MyLocation>,
        var moods: List<MyMood>
)