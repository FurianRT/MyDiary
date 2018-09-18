package com.furianrt.mydiary.utils

import java.util.*

fun generateUniqueId(): String {
    val uuid = UUID.randomUUID()
    return uuid.toString()
}