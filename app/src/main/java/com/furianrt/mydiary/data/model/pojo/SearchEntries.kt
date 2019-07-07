package com.furianrt.mydiary.data.model.pojo

import com.furianrt.mydiary.data.model.*

class SearchEntries(
        var notes: List<MyNoteWithProp>,
        var tags: List<MyTag>,
        var categories: List<MyCategory>,
        var locations: List<MyLocation>,
        var moods: List<MyMood>
)