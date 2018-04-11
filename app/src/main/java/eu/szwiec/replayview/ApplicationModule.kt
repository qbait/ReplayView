package eu.szwiec.replayview

import android.app.Application
import android.content.Context

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import eu.szwiec.replayview.replay.ReplayFilesProvider
import eu.szwiec.replayview.replay.ReplayFilesProviderImpl
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
    fun provideFilesProvider(context: Context): ReplayFilesProvider {
        return ReplayFilesProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideReplayViewModelFactory(filesProvider: ReplayFilesProvider): ReplayViewModelFactory {
        return ReplayViewModelFactory(filesProvider)
    }

}
