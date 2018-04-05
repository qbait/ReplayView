package eu.szwiec.replayview

import eu.szwiec.replayview.replay.ReplayFile
import eu.szwiec.replayview.replay.Type

interface FilesProvider {
    fun provide(zipPath: String?, types: List<Type>): List<ReplayFile>
}
