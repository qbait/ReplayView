package eu.szwiec.replayview.replay

import android.content.Context

class ReplayFilesProviderImpl: ReplayFilesProvider {
    override fun provide(zipPath: String?, types: List<ReplayType>): List<ReplayFile> {
        val btStream = context.assets.open("sample.edyuid0")
        val wifiStream = context.assets.open("sample.wifi0")
        val btFile = ReplayFile(btStream, ReplayType.BLUETOOTH)
        val wifiFile = ReplayFile(wifiStream, ReplayType.WIFI)

        return listOf(btFile, wifiFile)
    }

    private val context: Context;

    constructor(context: Context) {
        this.context = context
    }
}
