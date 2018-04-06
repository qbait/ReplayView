package eu.szwiec.replayview.replay

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ReplayViewModelTest {
    @Mock
    private lateinit var filesProviderMock: ReplayFilesProvider
    @Mock
    private lateinit var isPlayingObserver: Observer<Boolean>
    @Mock
    private lateinit var speedObserver: Observer<Int>
    @Mock
    private lateinit var stateObserver: Observer<State>

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
    fun togglePlayPause() {
        viewModel.isPlayingLD.observeForever(isPlayingObserver)
        viewModel.togglePlayPause()
        verify(isPlayingObserver).onChanged(true)
        viewModel.togglePlayPause()
        verify(isPlayingObserver).onChanged(false)
    }

    @Test
    fun changeSpeed() {
        viewModel.speedLD.observeForever(speedObserver)

        assertEquals(SPEEDS[0], viewModel.speedLD.value)

        viewModel.changeSpeed()
        verify(speedObserver).onChanged(SPEEDS[1])
        viewModel.changeSpeed()
        verify(speedObserver).onChanged(SPEEDS[2])
        viewModel.changeSpeed()
        verify(speedObserver).onChanged(SPEEDS[3])
        viewModel.changeSpeed()
        verify(speedObserver).onChanged(SPEEDS[0])
    }

    @Test
    fun onOpenButtonClick() {
        viewModel.stateLD.observeForever(stateObserver)

        viewModel.onOpenButtonClick()
        verify(stateObserver).onChanged(State.PICKING_TYPE)
    }

    @Test
    fun onTypePickedClick() {
        viewModel.stateLD.observeForever(stateObserver)

        viewModel.onTypePicked()
        verify(stateObserver).onChanged(State.PICKING_FILE)
    }

    //TODO Coroutines test
    @Test
    fun onFilePicked() {
//        viewModel.stateLD.observeForever(stateObserver)
//
//        viewModel.onFilePicked("")
//        verify(stateObserver).onChanged(State.PROCESSING)
    }

    @Test
    fun playingTime() {
        val playingTime = viewModel.formatPlayingTime(1, SAMPLE_EVENTS)
        assertEquals("07:40", playingTime)
    }

    @Test
    fun totalTime() {
        val totalTime = viewModel.formatTotalTime(SAMPLE_EVENTS)
        assertEquals("09:30", totalTime)
    }


}