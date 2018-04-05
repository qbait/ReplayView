package eu.szwiec.replayview.replay

import eu.szwiec.replayview.FilesProvider
import eu.szwiec.replayview.utils.NonNullLiveData
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


class ReplayViewModelTest {
    @Mock private lateinit var filesProviderMock: FilesProvider
    @Mock private lateinit var isPlayingLDMock: NonNullLiveData<Boolean>

    private lateinit var viewModel: ReplayViewModel

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        viewModel = ReplayViewModel(filesProviderMock)
    }

    @Test
    fun testTogglePlayPause() {
//        Mockito.`when`(isPlayingLDMock.value).thenReturn(false)
//        viewModel.togglePlayPause()
//        Mockito.verify(isPlayingLDMock.value).compareTo(true)
    }
}