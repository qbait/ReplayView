package eu.szwiec.replayview.replay

import java.util.concurrent.TimeUnit

fun ReplayEvent.msTimestamp() : Long {
    return TimeUnit.NANOSECONDS.toMillis(this.nanoTimestamp)
}