package eu.szwiec.replayview.replay

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.support.annotation.VisibleForTesting
import eu.szwiec.replayview.utils.NonNullLiveData
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.apache.commons.lang3.time.DurationFormatUtils
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.info
import java.util.concurrent.TimeUnit

class ReplayViewModel(filesProvider: ReplayFilesProvider) : ViewModel(), AnkoLogger {

    private val playingThread: Thread
    private var pickedDataTypes = emptyList<ReplayType>()

    val stateLD = NonNullLiveData(State.NOT_READY)
    val progressLD = NonNullLiveData(0)
    val speedLD = NonNullLiveData(SPEEDS[0])
    private val eventsLD = NonNullLiveData(emptyList<ReplayEvent>())
    val isPlayingLD = NonNullLiveData(false)

    val playingTimeLD: LiveData<String> = Transformations.map(progressLD, { progress -> formatPlayingTime(progress, eventsLD.value) })
    val totalTimeLD: LiveData<String> = Transformations.map(eventsLD, { events -> formatTotalTime(events) })
    val maxProgressLD: LiveData<Int> = Transformations.map(eventsLD, { events -> events.size - 1 })
    val isPlayingEnabledLD: LiveData<Boolean> = Transformations.map(stateLD, { state -> state == State.READY })

    val filesProvider: ReplayFilesProvider

    init {
        playingThread = initPlayingThread()
        this.filesProvider = filesProvider
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
        val currentSpeedId = SPEEDS.indexOf(currentSpeed)
        nextSpeedId = if (currentSpeedId == SPEEDS.size - 1) {
            0
        } else {
            currentSpeedId + 1
        }
        speedLD.value = SPEEDS[nextSpeedId]
    }


    fun onOpenButtonClick() {
        stateLD.value = State.PICKING_TYPE
    }

    fun setPickedTypes(types: Array<CharSequence>) {
        pickedDataTypes = ReplayType.getTypes(types)
    }

    fun onTypePicked() {
        stateLD.value = State.PICKING_FILE
    }

    fun onFilePicked(path: String?) {
        stateLD.value = State.PROCESSING
        importData(path, pickedDataTypes)
    }

    fun dialogDismissed() {
        if (eventsLD.value.isNotEmpty())
            stateLD.value = State.READY
        else
            stateLD.value = State.NOT_READY
    }

    private fun importData(path: String?, pickedDataTypes: List<ReplayType>) {
        async(UI) {
            val replayEvents = bg {
                val files = filesProvider.provide(path, pickedDataTypes)
                info("DUPA files = $files")

                val events = ReplayEventsParser.getEvents(files)
                events.sortBy { it.nanoTimestamp }
                info("events = $events")
                events
            }
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun formatPlayingTime(progress: Int, events: List<ReplayEvent>): String {
        if (events.isEmpty()) return ""

        val firstTimestamp = events[0].msTimestamp()
        val currentTimestamp = events[progress].msTimestamp()

        return format(currentTimestamp - firstTimestamp)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun formatTotalTime(events: List<ReplayEvent>): String {
        if (events.isEmpty()) return ""

        val firstTimestamp = events[0].msTimestamp()
        val lastTimestamp = events[events.size - 1].msTimestamp()

        return format(lastTimestamp - firstTimestamp)
    }

    private fun format(timestampMs: Long): String {
        return DurationFormatUtils.formatDuration(timestampMs, "mm:ss")
    }

    private fun ReplayEvent.msTimestamp(): Long {
        return TimeUnit.NANOSECONDS.toMillis(this.nanoTimestamp)
    }
}