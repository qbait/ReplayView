package eu.szwiec.replayview.replay

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.apache.commons.lang3.time.DurationFormatUtils
import org.jetbrains.anko.coroutines.experimental.bg
import timber.log.Timber

class ReplayViewModel : ViewModel() {
    val importDataManager: ImportDataManager
    val playingThread: Thread

    enum class State {
        NOT_READY, PICKING_TYPE, PICKING_FILE, PROCESSING, READY, ERROR
    }

    val speeds = listOf(1, 4, 16, 32)
    val availableDataTypes = listOf(ImportDataManager.TYPE_WIFI, ImportDataManager.TYPE_GPS, ImportDataManager.TYPE_BLUETOOTH, ImportDataManager.TYPE_SENSOR)

    var pickedDataTypes: Array<CharSequence> = emptyArray()
    var isPlaying = false

    val stateLD = MutableLiveData<State>() //TODO check LiveData nullability
    val progressLD = MutableLiveData<Int>()
    val speedLD = MutableLiveData<Int>()
    var eventsLD = MutableLiveData<List<ReplayEvent>>()

    val playingTimeLD = MutableLiveData<String>()
    val totalTimeLD = MutableLiveData<String>()
    val isPlayingEnabledLD = MutableLiveData<Boolean>()

    init {
        importDataManager = ImportDataManager()
        playingThread = initPlayingThread()

        stateLD.value = State.NOT_READY
        progressLD.value = 0
        speedLD.value = 1
        eventsLD.value = emptyList()

        progressLD.observeForever({ progress -> playingTimeLD.value = formatPlayingTime(progress!!, eventsLD.value!!) }) //TODO checkout if observeForever won't leak
        eventsLD.observeForever({ events -> totalTimeLD.value = formatTotalTime(events!!) })
        stateLD.observeForever({ state -> isPlayingEnabledLD.value = state == State.READY })
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
            val replayEvents = bg { importDataManager.importData(path, pickedDataTypes) }
            onPostExecute(replayEvents.await())
        }
    }

    private fun onPostExecute(events: List<ReplayEvent>) {
        eventsLD.value = events

        if (events.size > 0) {
            stateLD.value = State.READY
        } else {
            stateLD.value = State.ERROR
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

    fun dialogDismissed() {
        if(eventsLD.value!!.size > 0)
            stateLD.value = State.READY
        else
            stateLD.value = State.NOT_READY
    }

    private fun initPlayingThread(): Thread {
        return Thread {
            while (progressLD.value!! < eventsLD.value!!.size!! - 1) {
                if (isPlaying) {
                    val progress = progressLD.value!!
                    val events = eventsLD.value!!
                    val event = events.get(progress)

                    Timber.d("REPLAY post event: %s", event) //TODO post on the BUS

                    if (progress == events.size) {
                        stop()
                    } else {
                        progressLD.postValue(progress + 1)
                        val diffTimeMs = events.get(progress + 1).msTimestamp() - events.get(progress).msTimestamp()
                        Thread.sleep(diffTimeMs / speedLD.value!!)
                    }
                }
            }
        }
    }

    private fun formatPlayingTime(progress: Int, events: List<ReplayEvent>): String {
        if (events.size == 0) return ""

        val firstTimestamp = events.get(0).msTimestamp()
        val currentTimestamp = events.get(progress).msTimestamp()

        return format(currentTimestamp - firstTimestamp)
    }

    private fun formatTotalTime(events: List<ReplayEvent>): String {
        if (events.size == 0) return ""

        val firstTimestamp = events.get(0).msTimestamp()
        val lastTimestamp = events.get(events.size-1).msTimestamp()

        return format(lastTimestamp - firstTimestamp)
    }

    fun format(timestampMs: Long): String {
        return DurationFormatUtils.formatDuration(timestampMs, "mm:ss")
    }
}