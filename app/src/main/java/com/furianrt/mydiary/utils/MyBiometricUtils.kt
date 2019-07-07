package com.furianrt.mydiary.utils

import android.content.Context
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

fun Context.isFingerprintHardwareSupported() = FingerprintManagerCompat.from(this).isHardwareDetected

fun Context.isFingerprintEnabled() = FingerprintManagerCompat.from(this).hasEnrolledFingerprints()

fun Context.isFingerprintAvailable() = isFingerprintHardwareSupported() && isFingerprintEnabled()
