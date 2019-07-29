/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.check

import android.util.Patterns
import javax.inject.Inject

class CheckEmailUseCase @Inject constructor() {

    fun invoke(email: String): Boolean =
            email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}