package eu.szwiec.replayview.replay

import android.content.Context

object FilesProvider {

    fun getFiles(context: Context, zipPath: String?, types: List<Type>): List<ReplayFile> {
        val btStream = context.assets.open("sample.edyuid0")
        val wifiStream = context.assets.open("sample.wifi0")
        val btFile = ReplayFile(btStream, Type.BLUETOOTH)
        val wifiFile = ReplayFile(wifiStream, Type.WIFI)

        return listOf(btFile, wifiFile)
    }
}
