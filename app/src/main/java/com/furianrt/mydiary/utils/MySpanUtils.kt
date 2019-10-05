/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.utils

import android.graphics.Typeface
import android.text.Editable
import android.text.ParcelableSpan
import android.text.style.*
import com.furianrt.mydiary.data.entity.MyTextSpan

fun String.applyTextSpans(spans: List<MyTextSpan>): Editable =
        Editable.Factory().newEditable(this).apply {
            spans.forEach { textSpan ->
                if (textSpan.endIndex <= length) {
                    when (textSpan.type) {
                        MyTextSpan.TYPE_BOLD_TEXT -> setSpan(StyleSpan(Typeface.BOLD),
                                textSpan.startIndex,
                                textSpan.endIndex,
                                Editable.SPAN_INCLUSIVE_INCLUSIVE)
                        MyTextSpan.TYPE_ITALIC_TEXT -> setSpan(StyleSpan(Typeface.ITALIC),
                                textSpan.startIndex,
                                textSpan.endIndex,
                                Editable.SPAN_INCLUSIVE_INCLUSIVE)
                        MyTextSpan.TYPE_STRIKETHROUGH_TEXT -> setSpan(
                                StrikethroughSpan(),
                                textSpan.startIndex,
                                textSpan.endIndex,
                                Editable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        MyTextSpan.TYPE_BIG_TEXT ->
                            textSpan.size?.let { size ->
                                setSpan(
                                        RelativeSizeSpan(size),
                                        textSpan.startIndex,
                                        textSpan.endIndex,
                                        Editable.SPAN_INCLUSIVE_INCLUSIVE
                                )
                            }
                        MyTextSpan.TYPE_TEXT_COLOR ->
                            textSpan.color?.let { color ->
                                setSpan(
                                        ForegroundColorSpan(color),
                                        textSpan.startIndex,
                                        textSpan.endIndex,
                                        Editable.SPAN_INCLUSIVE_INCLUSIVE
                                )
                            }
                        MyTextSpan.TYPE_FILL_COLOR ->
                            textSpan.color?.let { color ->
                                setSpan(
                                        BackgroundColorSpan(color),
                                        textSpan.startIndex,
                                        textSpan.endIndex,
                                        Editable.SPAN_INCLUSIVE_INCLUSIVE
                                )
                            }
                    }
                }
            }
        }

fun Editable.getTextSpans(): List<MyTextSpan> =
        mutableListOf<MyTextSpan>().apply {
            getSpans(0, length, ParcelableSpan::class.java).forEach { span ->
                when {
                    span is StyleSpan && span.style == Typeface.BOLD -> add(MyTextSpan(
                            type = MyTextSpan.TYPE_BOLD_TEXT,
                            startIndex = getSpanStart(span),
                            endIndex = getSpanEnd(span)
                    ))
                    span is StyleSpan && span.style == Typeface.ITALIC -> add(MyTextSpan(
                            type = MyTextSpan.TYPE_ITALIC_TEXT,
                            startIndex = getSpanStart(span),
                            endIndex = getSpanEnd(span)
                    ))
                    span is StrikethroughSpan -> add(MyTextSpan(
                            type = MyTextSpan.TYPE_STRIKETHROUGH_TEXT,
                            startIndex = getSpanStart(span),
                            endIndex = getSpanEnd(span)
                    ))
                    span is RelativeSizeSpan -> add(MyTextSpan(
                            type = MyTextSpan.TYPE_BIG_TEXT,
                            startIndex = getSpanStart(span),
                            endIndex = getSpanEnd(span),
                            size = span.sizeChange
                    ))
                    span is ForegroundColorSpan -> add(MyTextSpan(
                            type = MyTextSpan.TYPE_TEXT_COLOR,
                            startIndex = getSpanStart(span),
                            endIndex = getSpanEnd(span),
                            color = span.foregroundColor
                    ))
                    span is BackgroundColorSpan -> add(MyTextSpan(
                            type = MyTextSpan.TYPE_FILL_COLOR,
                            startIndex = getSpanStart(span),
                            endIndex = getSpanEnd(span),
                            color = span.backgroundColor
                    ))
                }
            }
        }