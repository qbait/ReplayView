package eu.szwiec.replayview

import android.content.Context
import eu.szwiec.replayview.replay.ReplayType

class ReplayRobot(private val context: Context) : BaseTestRobot() {

    fun clickPickFileButton() = apply {
        clickView(R.id.pick_file_button)
    }

    fun clickSpeedButton() = apply {
        clickView(R.id.speed_button)
    }

    fun isSpeedCorrect(speed: Int) = apply {
        checkViewHasText(R.id.speed_button, String.format(context.getString(R.string.replay_speed_mask, speed)))
    }

    fun chooseWifi() = apply {
        clickView(ReplayType.WIFI.toString())
    }

    fun chooseBt() = apply {
        clickView(ReplayType.BLUETOOTH.toString())
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
        checkViewHasText(R.id.total_time, R.string.total_time)
    }

    fun isPlayingTimeCorrect() = apply {
        checkViewHasText(R.id.playing_time, R.string.starting_time)
    }

    fun isPlaybackDisabled() = apply {
        isViewDisabled(R.id.play_pause_button)
        isViewDisabled(R.id.seekbar)
        isViewDisabled(R.id.speed_button)
    }

    fun isPlaybackEnabled() = apply {
        isViewEnabled(R.id.play_pause_button)
        isViewEnabled(R.id.seekbar)
        isViewEnabled(R.id.speed_button)
    }

    fun play() = apply {
        clickView(R.id.play_pause_button)
    }

    fun isPlayIconShown() = apply {
        isViewUnchecked(R.id.play_pause_button)
    }

    fun isPauseIconShown() = apply {
        isViewChecked(R.id.play_pause_button);
    }
}
