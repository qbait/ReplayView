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
    private MutableLiveData<Boolean> mIsPlayingLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsProcessingLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ReplayEvent>> mEventsLiveData = new MutableLiveData<>();

    public final List<String> availableDataTypes = Collections.unmodifiableList(Arrays.asList(ImportDataManager.TYPE_WIFI, ImportDataManager.TYPE_GPS, ImportDataManager.TYPE_BLUETOOTH, ImportDataManager.TYPE_SENSOR));
    public CharSequence[] mPickedDataTypes;
    private ImportDataManager mImportDataManager;
    private Subscription mSubscription;

    public ReplayViewModel(@NonNull Application application) {
        super(application);

        mIsPlayingLiveData.setValue(false);
        mImportDataManager = new ImportDataManager(application);
    }

    public void toggle() {
        mIsPlayingLiveData.setValue(!mIsPlayingLiveData.getValue());
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

}
