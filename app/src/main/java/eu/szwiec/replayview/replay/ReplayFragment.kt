package eu.szwiec.replayview.replay

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast

import com.afollestad.materialdialogs.MaterialDialog
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog

import java.io.File

import eu.szwiec.replayview.R
import eu.szwiec.replayview.databinding.FragmentReplayBinding
import rm.com.youtubeplayicon.PlayIconDrawable

import rm.com.youtubeplayicon.PlayIconDrawable.IconState.PAUSE
import rm.com.youtubeplayicon.PlayIconDrawable.IconState.PLAY

class ReplayFragment : Fragment() {

    private var mViewModel: ReplayViewModel? = null
    private var mBinding: FragmentReplayBinding? = null

    private var mPlayPauseIconDrawable: PlayIconDrawable? = null
    private var mProgressDialog: MaterialDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_replay, container, false)
        mViewModel = ViewModelProviders.of(this).get(ReplayViewModel::class.java)
        mBinding?.let {
            it.viewModel = mViewModel
            it.setLifecycleOwner(this)
        }

        val view = mBinding?.root

        mViewModel!!.isProcessingLiveData.observe(this, Observer { isProcessing ->
            if (isProcessing!!) {
                if (mProgressDialog == null) {
                    mProgressDialog = buildProgressDialog()
                }
                mProgressDialog!!.show()
            } else {
                mProgressDialog!!.dismiss()
            }
        })

        mViewModel!!.eventsLiveData.observe(this, Observer { events ->
            if (events!!.size != 0) {
                enablePlayackControls(true)
            } else {
                enablePlayackControls(false)
                Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show()
            }
        })

        mViewModel!!.isPlayingLiveData.observe(this, Observer { isPlaying ->
            if (isPlaying!!) {
                mPlayPauseIconDrawable!!.animateToState(PAUSE)
            } else {
                mPlayPauseIconDrawable!!.animateToState(PLAY)
            }
        })

        mViewModel!!.progressLiveData.observe(this, Observer { progress -> mBinding!!.seekbar.progress = progress!! })

        init()

        return view
    }

    private fun init() {
        mPlayPauseIconDrawable = buildPlayPause()
        enablePlayackControls(true)

        mBinding!!.pickFileButton.setOnClickListener { v -> buildDataTypeDialog().show() }

        mBinding!!.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mViewModel!!.setProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun buildProgressDialog(): MaterialDialog {
        return MaterialDialog.Builder(this.context!!)
                .content("Loading...")
                .progress(true, 0)
                .cancelable(false)
                .build()
    }

    private fun buildPlayPause(): PlayIconDrawable {
        return PlayIconDrawable.builder()
                .withInterpolator(FastOutSlowInInterpolator())
                .withDuration(300)
                .withInitialState(PLAY)
                .into(mBinding!!.playPauseButton)
    }


    private fun buildFilePickerDialog(): FilePickerDialog {
        val properties = DialogProperties()

        properties.selection_mode = DialogConfigs.SINGLE_MODE
        properties.selection_type = DialogConfigs.FILE_SELECT
        properties.root = Environment.getExternalStorageDirectory()
        properties.error_dir = File(DialogConfigs.DEFAULT_DIR)
        properties.offset = File(DialogConfigs.DEFAULT_DIR)
        properties.extensions = arrayOf("zip")

        val filePickerDialog = FilePickerDialog(context, properties)
        filePickerDialog.setTitle("Select a File")

        filePickerDialog.setDialogSelectionListener { files -> mViewModel!!.zipPicked(files[0]) }

        return filePickerDialog
    }

    private fun buildDataTypeDialog(): MaterialDialog {

        return MaterialDialog.Builder(context!!)
                .title("Choose data type")
                .items(mViewModel!!.availableDataTypes)
                .itemsCallbackMultiChoice(null) { dialog, which, text ->
                    mViewModel!!.typesPicked(text)
                    true
                }
                .positiveText("Choose")
                .onPositive { dialog, which -> buildFilePickerDialog().show() }
                .build()
    }

    private fun enablePlayackControls(enabled: Boolean) {
        mBinding!!.playPauseButton.isEnabled = enabled
        mBinding!!.seekbar.isEnabled = enabled
        mBinding!!.speedButton.isEnabled = enabled

        if (enabled) {
            mPlayPauseIconDrawable!!.setColor(resources.getColor(R.color.colorPrimaryDark))
        } else {
            mPlayPauseIconDrawable!!.setColor(resources.getColor(R.color.dbc_light_grey))
        }
    }
}
