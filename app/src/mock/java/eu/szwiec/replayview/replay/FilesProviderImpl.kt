package eu.szwiec.replayview.replay

import android.content.Context
import eu.szwiec.replayview.FilesProvider

class FilesProviderImpl: FilesProvider {
    override fun provide(zipPath: String?, types: List<Type>): List<ReplayFile> {
        val btStream = context.assets.open("sample.edyuid0")
        val wifiStream = context.assets.open("sample.wifi0")
        val btFile = ReplayFile(btStream, Type.BLUETOOTH)
        val wifiFile = ReplayFile(wifiStream, Type.WIFI)

        return listOf(btFile, wifiFile)
    }

    private val context: Context;

    constructor(context: Context) {
        this.context = context
    }
}
