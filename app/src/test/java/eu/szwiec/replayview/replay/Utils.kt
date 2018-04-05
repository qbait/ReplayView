package eu.szwiec.replayview.replay

import java.io.*

private val ASSET_BASE_PATH = "../app/src/mock/assets/"

fun openAsset(filename: String): InputStream {
    return FileInputStream(ASSET_BASE_PATH + filename)
}