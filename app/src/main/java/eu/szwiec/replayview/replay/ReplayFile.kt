package eu.szwiec.replayview.replay

import java.io.InputStream

data class ReplayFile(val stream: InputStream, val type: ReplayType)