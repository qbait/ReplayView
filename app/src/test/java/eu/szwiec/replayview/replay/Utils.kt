package eu.szwiec.replayview.replay

import eu.szwiec.replayview.otto.EddystoneUidPacketEvent
import java.io.*

private val ASSET_BASE_PATH = "../app/src/mock/assets/"

fun openAsset(filename: String): InputStream {
    return FileInputStream(ASSET_BASE_PATH + filename)
}

val SAMPLE_EVENTS = listOf(
        EddystoneUidPacketEvent("", 0, 24433410138669),
        EddystoneUidPacketEvent("", 0, 24893846132764),
        EddystoneUidPacketEvent("", 0, 25004023082160)
)