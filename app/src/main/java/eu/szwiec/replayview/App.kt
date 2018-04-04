package eu.szwiec.replayview

import android.app.Application

import java.io.File

import eu.szwiec.replayview.replay.FilesProvider
import eu.szwiec.replayview.replay.ReplayFile
import eu.szwiec.replayview.replay.Type

open class App : Application() {

    open fun provideFiles(zipPath: String, extensions: List<Type>): List<ReplayFile> = FilesProvider.getFiles(zipPath, extensions)

}
