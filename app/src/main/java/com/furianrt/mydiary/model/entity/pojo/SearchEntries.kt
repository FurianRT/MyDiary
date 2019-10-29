/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.entity.pojo

import com.furianrt.mydiary.model.entity.*

class SearchEntries(
        var notes: List<MyNoteWithProp>,
        var tags: List<MyTag>,
        var categories: List<MyCategory>,
        var locations: List<MyLocation>?,
        var moods: List<MyMood>?
)