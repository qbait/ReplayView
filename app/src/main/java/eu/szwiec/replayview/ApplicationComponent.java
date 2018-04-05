package eu.szwiec.replayview;

import javax.inject.Singleton;

import dagger.Component;
import eu.szwiec.replayview.replay.ReplayFragment;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(ReplayFragment target);
}
