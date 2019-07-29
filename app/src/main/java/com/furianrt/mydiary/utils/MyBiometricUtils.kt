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

import android.content.Context
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

fun Context.isFingerprintHardwareSupported() = FingerprintManagerCompat.from(this).isHardwareDetected

fun Context.isFingerprintEnabled() = FingerprintManagerCompat.from(this).hasEnrolledFingerprints()

fun Context.isFingerprintAvailable() = isFingerprintHardwareSupported() && isFingerprintEnabled()
