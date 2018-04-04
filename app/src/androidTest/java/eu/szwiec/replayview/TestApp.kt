package eu.szwiec.replayview

import eu.szwiec.replayview.replay.ReplayFile
import eu.szwiec.replayview.replay.Type

class TestApp : App() {
    private lateinit var files: List<ReplayFile>

    fun setFiles(files: List<ReplayFile>) {
        this.files = files
    }

    override fun provideFiles(zipPath: String?, types: List<Type>): List<ReplayFile> {
        return files
    }

}
