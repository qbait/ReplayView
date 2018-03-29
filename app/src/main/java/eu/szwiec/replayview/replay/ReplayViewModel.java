package eu.szwiec.replayview.replay;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ReplayViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> mIsProcessingLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ReplayEvent>> mEventsLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> mIsPlayingLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> mProgressLiveData = new MutableLiveData<>();

    public MutableLiveData<String> mPlayingTimeLiveData = new MutableLiveData<>();
    public MutableLiveData<String> mTotalTimeLiveData = new MutableLiveData<>();

    public final List<String> availableDataTypes = Collections.unmodifiableList(Arrays.asList(ImportDataManager.TYPE_WIFI, ImportDataManager.TYPE_GPS, ImportDataManager.TYPE_BLUETOOTH, ImportDataManager.TYPE_SENSOR));

    private CharSequence[] mPickedDataTypes;
    private ImportDataManager mImportDataManager;
    private Subscription mSubscription;

    public ReplayViewModel(@NonNull Application application) {
        super(application);

        mIsPlayingLiveData.setValue(false);
        mImportDataManager = new ImportDataManager(application);
    }

    public void toggle() {
        if (mIsPlayingLiveData.getValue() == true) {
            pause();
        } else {
            play();
        }
    }

    public MutableLiveData<Boolean> getIsPlayingLiveData() {
        return mIsPlayingLiveData;
    }

    public MutableLiveData<Boolean> getIsProcessingLiveData() {
        return mIsProcessingLiveData;
    }

    public MutableLiveData<List<ReplayEvent>> getEventsLiveData() {
        return mEventsLiveData;
    }

    public MutableLiveData<Integer> getProgressLiveData() {
        return mProgressLiveData;
    }

    public void zipPicked(String path) {
        mIsProcessingLiveData.setValue(true);
        importData(path, mPickedDataTypes);
    }

    private void importData(String path, CharSequence[] pickedDataTypes) {

        mSubscription = mImportDataManager.getDataObservable(path, pickedDataTypes)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        venues -> onSuccess(venues),
                        throwable -> onError(throwable)
                );
    }

    public void typesPicked(CharSequence[] pickedTypes) {
        mPickedDataTypes = pickedTypes;
    }

    private void onSuccess(List<ReplayEvent> replayEvents) {
        mEventsLiveData.setValue(replayEvents);
        mIsProcessingLiveData.setValue(false);
    }

    private void onError(Throwable throwable) {
        Timber.e(throwable);
        mIsProcessingLiveData.setValue(false);
    }

    private Thread mPlayingThread;

    private void initPlayback() {
        setProgress(0);

        mPlayingThread = new Thread(() -> {
            while (true) {
                if (mIsPlayingLiveData.getValue()) {
                    Integer progress = mProgressLiveData.getValue();
                    if(progress == 100) {
                        stop();
                    } else {
                        postProgress(progress+1);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void play() {
        if (mPlayingThread == null) {
            initPlayback();
        }

        mIsPlayingLiveData.setValue(true);
        if (mPlayingThread.getState() == Thread.State.NEW) {
            mPlayingThread.start();
        }
    }

    private void pause() {
        mIsPlayingLiveData.setValue(false);
    }

    private void stop() {
        mIsPlayingLiveData.setValue(false);
        setProgress(0);
    }

    public String getPlayingTime(int progress) {
        return progress + "";
    }

    public String getTotalTime() {
        return "100";
    }

    public void setProgress(int progress) {
        mProgressLiveData.setValue(progress);
        mPlayingTimeLiveData.setValue(getPlayingTime(progress));
    }
    public void postProgress(int progress) {
        mProgressLiveData.postValue(progress);
        mPlayingTimeLiveData.postValue(getPlayingTime(progress));
    }

}
