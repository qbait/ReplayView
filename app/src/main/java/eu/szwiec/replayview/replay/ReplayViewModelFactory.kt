package eu.szwiec.replayview.replay

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class ReplayViewModelFactory(private val filesProvider: ReplayFilesProvider) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReplayViewModel(filesProvider) as T
    }
}
