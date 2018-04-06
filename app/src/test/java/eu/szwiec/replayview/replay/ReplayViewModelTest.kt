package eu.szwiec.replayview.replay

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import eu.szwiec.replayview.FilesProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


class ReplayViewModelTest {
    @Mock
    private lateinit var filesProviderMock: FilesProvider
    @Mock
    private lateinit var observer: Observer<Boolean>

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: ReplayViewModel

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        viewModel = ReplayViewModel(filesProviderMock)
    }

    @Test
    fun testTogglePlayPause() {
        viewModel.isPlayingLD.observeForever(observer)
        viewModel.togglePlayPause()
        verify(observer).onChanged(true)
        viewModel.togglePlayPause()
        verify(observer).onChanged(false)
    }
}