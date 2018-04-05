package eu.szwiec.replayview;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eu.szwiec.replayview.replay.FilesProviderImpl;
import eu.szwiec.replayview.replay.ReplayViewModelFactory;

@Module
public class ApplicationModule {

    private Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public FilesProvider provideFilesProvider(Context context) {
        return new FilesProviderImpl(context);
    }

    @Provides
    @Singleton
    public ReplayViewModelFactory provideReplayViewModelFactory(Context context) {
        FilesProvider filesProvider = new FilesProviderImpl(context);
        return new ReplayViewModelFactory(filesProvider);
    }

}
