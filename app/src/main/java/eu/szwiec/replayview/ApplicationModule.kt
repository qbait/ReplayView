package eu.szwiec.replayview

import android.app.Application
import android.content.Context

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import eu.szwiec.replayview.replay.FilesProviderImpl
import eu.szwiec.replayview.replay.ReplayViewModelFactory

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideFilesProvider(context: Context): FilesProvider {
        return FilesProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideReplayViewModelFactory(context: Context): ReplayViewModelFactory {
        val filesProvider = FilesProviderImpl(context)
        val speeds = context.resources.getIntArray(R.array.replaySpeeds)
        return ReplayViewModelFactory(filesProvider, speeds)
    }

}
