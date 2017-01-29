@file:Suppress("PackageDirectoryMismatch")
package com.evernote.android.job

fun JobRequest.scheduledTo() = this.scheduledAt + this.startMs

val JobRequest.rescheduled: Boolean
    get() = this.numFailures > 0

