/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UndoRedoEntry(
        val title: String = "",
        val content: String = "",
        val textSpans: List<MyTextSpan> = emptyList(),
        var current: Boolean = false
) : Parcelable