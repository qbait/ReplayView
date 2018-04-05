package eu.szwiec.replayview.replay

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import eu.szwiec.replayview.FilesProvider

class ReplayViewModelFactory(private val filesProvider: FilesProvider, private val speeds: IntArray) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReplayViewModel(filesProvider, speeds) as T
    }
}
