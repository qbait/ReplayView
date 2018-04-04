package eu.szwiec.replayview

import android.content.Context
import eu.szwiec.replayview.replay.FilesProvider
import eu.szwiec.replayview.replay.ReplayFile
import eu.szwiec.replayview.replay.Type

object Injection {

    fun provideFiles(context: Context, zipPath: String?, extensions: List<Type>): List<ReplayFile> = FilesProvider.getFiles(context, zipPath, extensions)

}
