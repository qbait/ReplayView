package eu.szwiec.replayview.replay;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import eu.szwiec.replayview.R;
import rm.com.youtubeplayicon.PlayIconDrawable;

import static rm.com.youtubeplayicon.PlayIconDrawable.IconState.PAUSE;
import static rm.com.youtubeplayicon.PlayIconDrawable.IconState.PLAY;

public class ReplayFragment extends Fragment {

    private ReplayViewModel mViewModel;

    private PlayIconDrawable mPlayPauseIconDrawable;
    private ImageButton mPlayPauseButton;
    private ImageButton mPickFileButton;
    private SeekBar mSeekbar;
    private SpeedButton mSpeedButton;

    private MaterialDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_replay, container, false);

        mViewModel = ViewModelProviders.of(this).get(ReplayViewModel.class);

        mViewModel.getIsPlayingLiveData().observe(this, isPlaying -> {
            if (isPlaying) {
                mPlayPauseIconDrawable.animateToState(PAUSE);
            } else {
                mPlayPauseIconDrawable.animateToState(PLAY);
            }
        });

        mViewModel.getIsProcessingLiveData().observe(this, isProcessing -> {
            if (isProcessing) {
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
        });

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        mPlayPauseButton = rootView.findViewById(R.id.play_pause_button);
        mPickFileButton = rootView.findViewById(R.id.pick_file_button);
        mSeekbar = rootView.findViewById(R.id.seekbar);
        mSpeedButton = rootView.findViewById(R.id.speed_button);

        mPlayPauseIconDrawable = buildPlayPause();
        mProgressDialog = buildProgressDialog();

        enablePlayackControls(false);

        mPlayPauseButton.setOnClickListener(v -> mViewModel.toggle());
        mPickFileButton.setOnClickListener(v -> buildDataTypeDialog().show());
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
                .into(mPlayPauseButton);
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
        mPlayPauseButton.setEnabled(enabled);
        mSeekbar.setEnabled(enabled);
        mSpeedButton.setEnabled(enabled);

        if (enabled) {
            mPlayPauseIconDrawable.setColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            mPlayPauseIconDrawable.setColor(getResources().getColor(R.color.dbc_light_grey));
        }
    }
}
