package eu.szwiec.replayview;

import javax.inject.Singleton;

import dagger.Component;
import eu.szwiec.replayview.replay.ReplayViewModel;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(ReplayViewModel target);
}
