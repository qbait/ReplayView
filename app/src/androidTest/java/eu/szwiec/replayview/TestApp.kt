package eu.szwiec.replayview

import eu.szwiec.replayview.replay.ReplayFile
import eu.szwiec.replayview.replay.Type

class TestApp : App() {

    override fun getFiles(zipPath: String, types: List<Type>): List<ReplayFile> {

        val btStream = assets.open("sample.edyuid0")
        val wifiStream = assets.open("sample.wifi0")
        val btFile = ReplayFile(btStream, Type.BLUETOOTH)
        val wifiFile = ReplayFile(wifiStream, Type.WIFI)

        return listOf(btFile, wifiFile)
    }

}
