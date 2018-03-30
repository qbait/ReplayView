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
import android.widget.Toast
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
    private lateinit var typePickerDialog: MaterialDialog
    private lateinit var filePickerDialog: FilePickerDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_replay, container, false)
        viewModel = ViewModelProviders.of(this).get(ReplayViewModel::class.java)
        binding.let {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }

        val view = binding.root

        progressDialog = buildProgressDialog()
        typePickerDialog = buildTypePickerDialog()
        filePickerDialog = buildFilePickerDialog()

        viewModel.stateLD.observe(this, Observer { state ->
            when(state) {
                ReplayViewModel.State.DISABLED -> enablePlayackControls(false)
                ReplayViewModel.State.PICKING_TYPE -> typePickerDialog.show()
                ReplayViewModel.State.PICKING_FILE -> filePickerDialog.show()
                ReplayViewModel.State.PROCESSING -> progressDialog.show()
                ReplayViewModel.State.ENABLED -> {
                    progressDialog.dismiss()
                    enablePlayackControls(true)
                }
                ReplayViewModel.State.ERROR -> {
                    progressDialog.dismiss()
                    enablePlayackControls(false)
                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.progressLD.observe(this, Observer { progress -> binding.seekbar.progress = progress!! })

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

        filePickerDialog.setDialogSelectionListener { files -> viewModel.onFilePicked(files[0]) }

        return filePickerDialog
    }

    private fun buildTypePickerDialog(): MaterialDialog {
        return MaterialDialog.Builder(context!!)
                .title("Choose data type")
                .items(viewModel.availableDataTypes)
                .itemsCallbackMultiChoice(null) { dialog, which, types ->
                    viewModel.setPickedTypes(types)
                    true
                }
                .positiveText("Choose")
                .onPositive { dialog, which -> viewModel.onTypePicked() }
                .build()
    }

    private fun enablePlayackControls(enabled: Boolean) {
        binding.playPauseButton.isEnabled = enabled
        binding.seekbar.isEnabled = enabled
        binding.speedButton.isEnabled = enabled
    }
}
