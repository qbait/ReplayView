package eu.szwiec.replayview.replay;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import eu.szwiec.replayview.R;
import eu.szwiec.replayview.databinding.FragmentReplayBinding;
import rm.com.youtubeplayicon.PlayIconDrawable;

import static rm.com.youtubeplayicon.PlayIconDrawable.IconState.PAUSE;
import static rm.com.youtubeplayicon.PlayIconDrawable.IconState.PLAY;

public class ReplayFragment extends Fragment {

    private ReplayViewModel mViewModel;
    private FragmentReplayBinding mBinding;

    private PlayIconDrawable mPlayPauseIconDrawable;
    private MaterialDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_replay, container, false);
        mBinding.setLifecycleOwner(this);

        View view = mBinding.getRoot();

        mViewModel = ViewModelProviders.of(this).get(ReplayViewModel.class);

        mViewModel.getIsProcessingLiveData().observe(this, isProcessing -> {
            if (isProcessing) {
                if(mProgressDialog == null) {
                    mProgressDialog = buildProgressDialog();
                }
                mProgressDialog.show();
            } else {
                mProgressDialog.dismiss();
            }
        });

        mViewModel.getEventsLiveData().observe(this, events -> {
            if (events.size() != 0) {
                enablePlayackControls(true);
            } else {
                enablePlayackControls(false);
                Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
            }

            mBinding.totalTime.setText(mViewModel.getTotalTime());
        });

        mViewModel.getIsPlayingLiveData().observe(this, isPlaying -> {
            if (isPlaying) {
                mPlayPauseIconDrawable.animateToState(PAUSE);
            } else {
                mPlayPauseIconDrawable.animateToState(PLAY);
            }
        });

        mViewModel.getProgressLiveData().observe(this, progress -> {
            mBinding.seekbar.setProgress(progress);
        });

        init();

        return view;
    }

    private void init() {
        mPlayPauseIconDrawable = buildPlayPause();
        enablePlayackControls(true);

        mBinding.playPauseButton.setOnClickListener(v -> mViewModel.toggle());
        mBinding.pickFileButton.setOnClickListener(v -> buildDataTypeDialog().show());

        mBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mViewModel.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private MaterialDialog buildProgressDialog() {
        return new MaterialDialog.Builder(this.getContext())
                .content("Loading...")
                .progress(true, 0)
                .cancelable(false)
                .build();
    }

    private PlayIconDrawable buildPlayPause() {
        return PlayIconDrawable.builder()
                .withInterpolator(new FastOutSlowInInterpolator())
                .withDuration(300)
                .withInitialState(PLAY)
                .into(mBinding.playPauseButton);
    }


    private FilePickerDialog buildFilePickerDialog() {
        DialogProperties properties = new DialogProperties();

        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = Environment.getExternalStorageDirectory();
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"zip"};

        FilePickerDialog filePickerDialog = new FilePickerDialog(getContext(), properties);
        filePickerDialog.setTitle("Select a File");

        filePickerDialog.setDialogSelectionListener(files -> {
            mViewModel.zipPicked(files[0]);
        });

        return filePickerDialog;
    }

    private MaterialDialog buildDataTypeDialog() {

        return new MaterialDialog.Builder(getContext())
                .title("Choose data type")
                .items(mViewModel.availableDataTypes)
                .itemsCallbackMultiChoice(null, (dialog, which, text) -> {
                    mViewModel.typesPicked(text);
                    return true;
                })
                .positiveText("Choose")
                .onPositive((dialog, which) -> {
                    buildFilePickerDialog().show();
                })
                .build();
    }

    private void enablePlayackControls(boolean enabled) {
        mBinding.playPauseButton.setEnabled(enabled);
        mBinding.seekbar.setEnabled(enabled);
        mBinding.speedButton.setEnabled(enabled);

        if (enabled) {
            mPlayPauseIconDrawable.setColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            mPlayPauseIconDrawable.setColor(getResources().getColor(R.color.dbc_light_grey));
        }
    }
}
