package eu.szwiec.replayview.replay

import java.util.ArrayList
import java.util.concurrent.TimeUnit

fun ReplayEvent.msTimestamp() : Long {
    return TimeUnit.NANOSECONDS.toMillis(this.nanoTimestamp)
}

fun Array<CharSequence>.toTypeList(): List<Type> {
    val typeList = ArrayList<Type>()
    for (text in this) {
        val type = Type.getType(text.toString())
        typeList.add(type)
    }
    return typeList
}