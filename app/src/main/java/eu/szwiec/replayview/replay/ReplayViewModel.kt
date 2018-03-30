package eu.szwiec.replayview.replay

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*

class ReplayViewModel : ViewModel() {
    val importDataManager: ImportDataManager
    val playingThread: Thread

    enum class State {
        DISABLED, PICKING_TYPE, PICKING_FILE, PROCESSING, ENABLED, ERROR
    }

    val speeds = listOf(1, 4, 16, 32)
    val availableDataTypes = Collections.unmodifiableList(Arrays.asList(ImportDataManager.TYPE_WIFI, ImportDataManager.TYPE_GPS, ImportDataManager.TYPE_BLUETOOTH, ImportDataManager.TYPE_SENSOR))

    var pickedDataTypes: Array<CharSequence>? = null
    var isPlaying = false

    val stateLD = MutableLiveData<State>()
    val progressLD = MutableLiveData<Int>()
    val speedLD = MutableLiveData<Int>()
    var eventsLD = MutableLiveData<List<ReplayEvent>>()

    val playingTimeLD = MutableLiveData<String>()
    val totalTimeLD = MutableLiveData<String>()

    init {
        stateLD.value = State.ENABLED
        progressLD.value = 0
        speedLD.value = 1
        importDataManager = ImportDataManager()
        playingThread = initPlayingThread()

        progressLD.observeForever({ progress -> playingTimeLD.value = formatPlayingTime(progress) }) //TODO checkout if observeForever won't leak
        eventsLD.observeForever({ events -> totalTimeLD.value = formatTotalTime(events) })
    }

    fun togglePlayPause() {
        if (isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun changeSpeed() {
        val nextSpeedId: Int;

        val currentSpeed = speedLD.value
        val currentSpeedId = speeds.indexOf(currentSpeed)
        if (currentSpeedId == speeds.size - 1) {
            nextSpeedId = 0
        } else {
            nextSpeedId = currentSpeedId + 1
        }
        speedLD.value = speeds[nextSpeedId]
    }


    fun onOpenButtonClick() {
        stateLD.value = State.PICKING_TYPE
    }

    fun setPickedTypes(types: Array<CharSequence>?) {
        pickedDataTypes = types
    }

    fun onTypePicked() {
        stateLD.value = State.PICKING_FILE
    }

    fun onFilePicked(path: String) {
        stateLD.value = State.PROCESSING
        importData(path, pickedDataTypes)
    }

    private fun importData(path: String, pickedDataTypes: Array<CharSequence>?) {
        async(UI) {
            val replayEvents = bg { importDataManager.importData(path, pickedDataTypes) }
            onPostExecute(replayEvents.await())
        }
    }

    private fun onPostExecute(events: List<ReplayEvent>) {
        eventsLD.value = events

        if (events.size > 0) {
            stateLD.value = State.ENABLED
        } else {
            stateLD.value = State.ERROR
        }
    }

    private fun initPlayingThread(): Thread {
        return Thread {
            while (true) {
                if (isPlaying) {
                    val progress = progressLD.value!!
                    if (progress == 100) {
                        stop()
                    } else {
                        progressLD.postValue(progress + 1)
                        try {
                            Thread.sleep((500 / speedLD.value!!).toLong())
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun play() {
        isPlaying = true
        if (playingThread.state == Thread.State.NEW) {
            playingThread.start()
        }
    }

    private fun pause() {
        isPlaying = false
    }

    private fun stop() {
        isPlaying = false
        progressLD.value = 0
    }

    private fun formatPlayingTime(progress: Int?): String {
        return progress.toString()
    }

    private fun formatTotalTime(events: List<ReplayEvent>?): String {
        return events?.size.toString()
    }
}