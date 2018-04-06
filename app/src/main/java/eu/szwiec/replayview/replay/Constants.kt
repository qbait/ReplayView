package eu.szwiec.replayview.replay

enum class State {
    NOT_READY, PICKING_TYPE, PICKING_FILE, PROCESSING, READY, ERROR
}

val AVAILABLE_TYPES = listOf(ReplayType.WIFI, ReplayType.GPS, ReplayType.BLUETOOTH)
val SPEEDS = listOf(1, 4, 16, 32)