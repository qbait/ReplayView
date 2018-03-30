package eu.szwiec.replayview.replay

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.afollestad.materialdialogs.MaterialDialog
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import eu.szwiec.replayview.R
import eu.szwiec.replayview.databinding.FragmentReplayBinding
import java.io.File

class ReplayFragment : Fragment() {

    private lateinit var viewModel: ReplayViewModel
    private lateinit var binding: FragmentReplayBinding
    private lateinit var progressDialog: MaterialDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_replay, container, false)
        viewModel = ViewModelProviders.of(this).get(ReplayViewModel::class.java)
        binding.let {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }

        val view = binding.root

        viewModel.isProgressIndicatorVisibleLD.observe(this, Observer { isProcessing ->
            if (isProcessing!!) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })

        viewModel.eventsLD.observe(this, Observer { events ->
            if (events != null && events.size != 0) {
                enablePlayackControls(true)
            } else {
                enablePlayackControls(false)
            }
        })

        progressDialog = buildProgressDialog()

        viewModel.progressLD.observe(this, Observer { progress -> binding.seekbar.progress = progress!! })

        binding.pickFileButton.setOnClickListener { v -> buildDataTypeDialog().show() }

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        return view
    }

    private fun buildProgressDialog(): MaterialDialog {
        return MaterialDialog.Builder(this.context!!)
                .content("Loading...")
                .progress(true, 0)
                .cancelable(false)
                .build()
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

        filePickerDialog.setDialogSelectionListener { files -> viewModel.zipPicked(files[0]) }

        return filePickerDialog
    }

    private fun buildDataTypeDialog(): MaterialDialog {

        return MaterialDialog.Builder(context!!)
                .title("Choose data type")
                .items(viewModel.availableDataTypes)
                .itemsCallbackMultiChoice(null) { dialog, which, text ->
                    viewModel.typesPicked(text)
                    true
                }
                .positiveText("Choose")
                .onPositive { dialog, which -> buildFilePickerDialog().show() }
                .build()
    }

    private fun enablePlayackControls(enabled: Boolean) {
        binding.playPauseButton.isEnabled = enabled
        binding.seekbar.isEnabled = enabled
        binding.speedButton.isEnabled = enabled
    }
}
