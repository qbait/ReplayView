package eu.szwiec.replayview.replay

interface ReplayFilesProvider {
    fun provide(zipPath: String?, types: List<ReplayType>): List<ReplayFile>
}
