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

    val speeds = listOf(1, 4, 16, 32)
    val availableDataTypes = Collections.unmodifiableList(Arrays.asList(ImportDataManager.TYPE_WIFI, ImportDataManager.TYPE_GPS, ImportDataManager.TYPE_BLUETOOTH, ImportDataManager.TYPE_SENSOR))

    var pickedDataTypes: Array<CharSequence>? = null

    val isProgressIndicatorVisibleLD = MutableLiveData<Boolean>()

    val eventsLD = MutableLiveData<List<ReplayEvent>>()

    val isPlayingLD = MutableLiveData<Boolean>()
    val progressLD = MutableLiveData<Int>()
    val playingTimeLD = MutableLiveData<String>()
    val totalTimeLD = MutableLiveData<String>()
    val speedLD = MutableLiveData<Int>()

    init {
        isPlayingLD.value = false
        progressLD.value = 0
        eventsLD.value = null;
        speedLD.value = 1
        importDataManager = ImportDataManager()
        playingThread = initPlayingThread()
    }

    fun toggle() {
        if (isPlayingLD.value == true) {
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

    fun zipPicked(path: String) {
        isProgressIndicatorVisibleLD.value = true
        importData(path, pickedDataTypes)
    }

    private fun importData(path: String, pickedDataTypes: Array<CharSequence>?) {
        async(UI) {
            val replayEvents = bg { importDataManager.importData(path, pickedDataTypes) }
            onSuccess(replayEvents.await())
        }
    }

    private fun onSuccess(events: List<ReplayEvent>) {
        eventsLD.value = events
        isProgressIndicatorVisibleLD.value = false
        totalTimeLD.value = getTotalTime()
    }

    fun typesPicked(pickedTypes: Array<CharSequence>) {
        pickedDataTypes = pickedTypes
    }

    private fun initPlayingThread(): Thread {
        return Thread {
            while (true) {
                if (isPlayingLD.value!!) {
                    val progress = progressLD.value!!
                    if (progress == 100) {
                        stop()
                    } else {
                        postProgress(progress + 1)
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
        isPlayingLD.value = true
        if (playingThread.state == Thread.State.NEW) {
            playingThread.start()
        }
    }

    private fun pause() {
        isPlayingLD.value = false
    }

    private fun stop() {
        isPlayingLD.postValue(false)
        postProgress(0)
    }

    fun getPlayingTime(progress: Int): String {
        return progress.toString() + ""
    }

    fun getTotalTime(): String {
        return eventsLD.value?.size.toString()
    }

    fun setProgress(progress: Int) {
        progressLD.value = progress
        playingTimeLD.value = getPlayingTime(progress)
    }

    fun postProgress(progress: Int) {
        progressLD.postValue(progress)
        playingTimeLD.postValue(getPlayingTime(progress))
    }
}
