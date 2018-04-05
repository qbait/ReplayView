package eu.szwiec.replayview.replay;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import eu.szwiec.replayview.FilesProvider;

public class ReplayViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final FilesProvider filesProvider;

    public ReplayViewModelFactory(FilesProvider filesProvider) {
        this.filesProvider = filesProvider;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ReplayViewModel(filesProvider);
    }
}
