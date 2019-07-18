package com.furianrt.mydiary.domain.check

import android.util.Patterns
import javax.inject.Inject

class CheckEmailUseCase @Inject constructor() {

    fun invoke(email: String): Boolean =
            email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}