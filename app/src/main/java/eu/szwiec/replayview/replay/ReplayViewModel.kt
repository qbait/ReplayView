package eu.szwiec.replayview.replay

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.apache.commons.lang3.time.DurationFormatUtils
import org.jetbrains.anko.coroutines.experimental.bg

class ReplayViewModel : ViewModel() {
    enum class State {
        NOT_READY, PICKING_TYPE, PICKING_FILE, PROCESSING, READY, ERROR
    }

    private val speeds = listOf(1, 4, 16, 32)
    val availableDataTypes = listOf(ImportDataManager.TYPE_WIFI, ImportDataManager.TYPE_GPS, ImportDataManager.TYPE_BLUETOOTH, ImportDataManager.TYPE_SENSOR)

    private val playingThread: Thread
    private var pickedDataTypes: Array<CharSequence> = emptyArray()

    val stateLD = NonNullLiveData(State.NOT_READY)
    val progressLD = NonNullLiveData(0)
    val speedLD = NonNullLiveData(speeds[0])
    private val eventsLD = NonNullLiveData(emptyList<ReplayEvent>())
    val isPlayingLD = NonNullLiveData(false)

    val playingTimeLD: LiveData<String> = Transformations.map(progressLD, { progress -> formatPlayingTime(progress, eventsLD.value) })
    val totalTimeLD: LiveData<String> = Transformations.map(eventsLD, { events -> formatTotalTime(events) })
    val maxProgressLD: LiveData<Int> = Transformations.map(eventsLD, { events -> events.size - 1 })
    val isPlayingEnabledLD: LiveData<Boolean> = Transformations.map(stateLD, { state -> state == State.READY })

    init {
        playingThread = initPlayingThread()
    }

    fun togglePlayPause() {
        if (isPlayingLD.value) {
            pause()
        } else {
            play()
        }
    }

    fun changeSpeed() {
        val nextSpeedId: Int

        val currentSpeed = speedLD.value
        val currentSpeedId = speeds.indexOf(currentSpeed)
        nextSpeedId = if (currentSpeedId == speeds.size - 1) {
            0
        } else {
            currentSpeedId + 1
        }
        speedLD.value = speeds[nextSpeedId]
    }


    fun onOpenButtonClick() {
        stateLD.value = State.PICKING_TYPE
    }

    fun setPickedTypes(types: Array<CharSequence>) {
        pickedDataTypes = types
    }

    fun onTypePicked() {
        stateLD.value = State.PICKING_FILE
    }

    fun onFilePicked(path: String) {
        stateLD.value = State.PROCESSING
        importData(path, pickedDataTypes)
    }

    private fun importData(path: String, pickedDataTypes: Array<CharSequence>) {
        async(UI) {
            val replayEvents = bg { ImportDataManager.importData(path, pickedDataTypes) }
            onPostExecute(replayEvents.await())
        }
    }

    private fun onPostExecute(events: List<ReplayEvent>) {
        eventsLD.value = events

        if (events.isNotEmpty()) {
            stateLD.value = State.READY
        } else {
            stateLD.value = State.ERROR
        }
    }

    private fun play() {
        isPlayingLD.value = true
        if (playingThread.state == Thread.State.NEW) {
            playingThread.start()
        }
    }

    private fun pause() {
        isPlayingLD.postValue(false)
    }

    private fun stop() {
        isPlayingLD.postValue(false)
        progressLD.postValue(0)
    }

    fun dialogDismissed() {
        if (eventsLD.value.isNotEmpty())
            stateLD.value = State.READY
        else
            stateLD.value = State.NOT_READY
    }

    private fun initPlayingThread(): Thread {
        return Thread {
            while (true) {
                if (isPlayingLD.value) {
                    val progress = progressLD.value
                    val events = eventsLD.value
                    val event = events[progress]

                    //TODO post on the BUS

                    if (progress == maxProgressLD.value) {
                        stop()
                    } else {
                        progressLD.postValue(progress + 1)
                        val diffTimeMs = events[progress + 1].msTimestamp() - events[progress].msTimestamp()
                        Thread.sleep(diffTimeMs / speedLD.value)
                    }
                }
            }
        }
    }

    private fun formatPlayingTime(progress: Int, events: List<ReplayEvent>): String {
        if (events.isEmpty()) return ""

        val firstTimestamp = events[0].msTimestamp()
        val currentTimestamp = events[progress].msTimestamp()

        return format(currentTimestamp - firstTimestamp)
    }

    private fun formatTotalTime(events: List<ReplayEvent>): String {
        if (events.isEmpty()) return ""

        val firstTimestamp = events[0].msTimestamp()
        val lastTimestamp = events[events.size - 1].msTimestamp()

        return format(lastTimestamp - firstTimestamp)
    }

    private fun format(timestampMs: Long): String {
        return DurationFormatUtils.formatDuration(timestampMs, "mm:ss")
    }
}