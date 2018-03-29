package eu.szwiec.replayview.replay

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.*

class ReplayViewModel : ViewModel() {
    val isProcessingLiveData = MutableLiveData<Boolean>()
    val eventsLiveData = MutableLiveData<List<ReplayEvent>>()

    val isPlayingLiveData = MutableLiveData<Boolean>()
    val progressLiveData = MutableLiveData<Int>()

    val playingTimeLiveData = MutableLiveData<String>()
    val totalTimeLiveData = MutableLiveData<String>()
    val speedLiveData = MutableLiveData<Int>()

    val speeds = listOf(1, 4, 16, 32)

    val availableDataTypes = Collections.unmodifiableList(Arrays.asList(ImportDataManager.TYPE_WIFI, ImportDataManager.TYPE_GPS, ImportDataManager.TYPE_BLUETOOTH, ImportDataManager.TYPE_SENSOR))

    private var pickedDataTypes: Array<CharSequence>? = null
    private val importDataManager: ImportDataManager

    private val playingThread: Thread

    init {
        isPlayingLiveData.value = false
        progressLiveData.value = 0
        eventsLiveData.value = null;
        speedLiveData.value = 1
        importDataManager = ImportDataManager()
        playingThread = initPlayingThread()
    }

    fun toggle() {
        if (isPlayingLiveData.value == true) {
            pause()
        } else {
            play()
        }
    }

    fun changeSpeed() {
        val nextSpeedId: Int;

        val currentSpeed = speedLiveData.value
        val currentSpeedId = speeds.indexOf(currentSpeed)
        if (currentSpeedId == speeds.size - 1) {
            nextSpeedId = 0
        } else {
            nextSpeedId = currentSpeedId + 1
        }
        speedLiveData.value = speeds[nextSpeedId]
    }

    fun zipPicked(path: String) {
        isProcessingLiveData.value = true
        importData(path, pickedDataTypes)
    }

    private fun importData(path: String, pickedDataTypes: Array<CharSequence>?) {

        launch(UI) {
            val replayEvents: Deferred<List<ReplayEvent>> = async(CommonPool) {
                importDataManager.importData(path, pickedDataTypes)
            }
            onSuccess(replayEvents.await())
        }
    }

    private fun onSuccess(events: List<ReplayEvent>) {
        eventsLiveData.value = events
        isProcessingLiveData.value = false
        totalTimeLiveData.value = getTotalTime()
    }

    fun typesPicked(pickedTypes: Array<CharSequence>) {
        pickedDataTypes = pickedTypes
    }

    private fun initPlayingThread(): Thread {
        return Thread {
            while (true) {
                if (isPlayingLiveData.value!!) {
                    val progress = progressLiveData.value!!
                    if (progress == 100) {
                        stop()
                    } else {
                        postProgress(progress + 1)
                        try {
                            Thread.sleep((500 / speedLiveData.value!!).toLong())
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun play() {
        isPlayingLiveData.value = true
        if (playingThread.state == Thread.State.NEW) {
            playingThread.start()
        }
    }

    private fun pause() {
        isPlayingLiveData.value = false
    }

    private fun stop() {
        isPlayingLiveData.postValue(false)
        postProgress(0)
    }

    fun getPlayingTime(progress: Int): String {
        return progress.toString() + ""
    }

    fun getTotalTime(): String {
        return eventsLiveData.value?.size.toString()
    }

    fun setProgress(progress: Int) {
        progressLiveData.value = progress
        playingTimeLiveData.value = getPlayingTime(progress)
    }

    fun postProgress(progress: Int) {
        progressLiveData.postValue(progress)
        playingTimeLiveData.postValue(getPlayingTime(progress))
    }
}
