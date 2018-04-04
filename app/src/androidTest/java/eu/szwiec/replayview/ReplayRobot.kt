package eu.szwiec.replayview

import android.content.Context
import eu.szwiec.replayview.replay.Type

class ReplayRobot(private val context: Context) : BaseTestRobot() {

    fun clickPickFileButton() = apply {
        clickView(R.id.pick_file_button)
    }

    fun chooseWifi() = apply {
        clickView(Type.WIFI.toString())
    }

    fun chooseBt() = apply {
        clickView(Type.BLUETOOTH.toString())
    }

    fun confirmType() = apply {
        clickView(R.id.md_buttonDefaultPositive)
    }

    fun confirmFile() = apply {
        clickView(R.id.select)
    }

    fun hasFilePickerCorrectTitle() = apply {
        checkViewHasText(R.id.title, R.string.file_picker_title)
    }

    fun isTotalTimeCorrect() = apply {
        checkViewHasText(R.id.total_time, R.string.test_total_time)
    }
}
