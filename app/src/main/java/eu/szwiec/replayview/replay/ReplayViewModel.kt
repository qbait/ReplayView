package eu.szwiec.replayview.replay

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData

import java.util.Arrays
import java.util.Collections

import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber

class ReplayViewModel(application: Application) : AndroidViewModel(application) {
    val isProcessingLiveData = MutableLiveData<Boolean>()
    val eventsLiveData = MutableLiveData<List<ReplayEvent>>()

    val isPlayingLiveData = MutableLiveData<Boolean>()
    val progressLiveData = MutableLiveData<Int>()

    val playingTimeLiveData = MutableLiveData<String>()
    val totalTimeLiveData = MutableLiveData<String>()

    val availableDataTypes = Collections.unmodifiableList(Arrays.asList(ImportDataManager.TYPE_WIFI, ImportDataManager.TYPE_GPS, ImportDataManager.TYPE_BLUETOOTH, ImportDataManager.TYPE_SENSOR))

    private var pickedDataTypes: Array<CharSequence>? = null
    private val importDataManager: ImportDataManager
    private var subscription: Subscription? = null

    private var playingThread: Thread? = null

    init {
        isPlayingLiveData.value = false
        importDataManager = ImportDataManager(application)
    }

    fun toggle() {
        if (isPlayingLiveData.value == true) {
            pause()
        } else {
            play()
        }
    }

    fun zipPicked(path: String) {
        isProcessingLiveData.value = true
        importData(path, pickedDataTypes)
    }

    private fun importData(path: String, pickedDataTypes: Array<CharSequence>?) {

        subscription = importDataManager.getDataObservable(path, pickedDataTypes)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { venues -> onSuccess(venues) }
                ) { throwable -> onError(throwable) }
    }

    fun typesPicked(pickedTypes: Array<CharSequence>) {
        pickedDataTypes = pickedTypes
    }

    private fun onSuccess(replayEvents: List<ReplayEvent>) {
        eventsLiveData.value = replayEvents
        isProcessingLiveData.value = false
        totalTimeLiveData.value = getTotalTime()
    }

    private fun onError(throwable: Throwable) {
        Timber.e(throwable)
        isProcessingLiveData.value = false
    }

    private fun initPlayback() {
        setProgress(0)

        playingThread = Thread {
            while (true) {
                if (isPlayingLiveData.value!!) {
                    val progress = progressLiveData.value
                    if (progress == 100) {
                        stop()
                    } else {
                        postProgress(progress!! + 1)
                        try {
                            Thread.sleep(100)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        }
    }

    private fun play() {
        if (playingThread == null) {
            initPlayback()
        }

        isPlayingLiveData.value = true
        if (playingThread!!.state == Thread.State.NEW) {
            playingThread!!.start()
        }
    }

    private fun pause() {
        isPlayingLiveData.value = false
    }

    private fun stop() {
        isPlayingLiveData.value = false
        setProgress(0)
    }

    fun getPlayingTime(progress: Int): String {
        return progress.toString() + ""
    }

    fun getTotalTime(): String {
        return "100"
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
