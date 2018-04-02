package eu.szwiec.replayview

import eu.szwiec.replayview.replay.Type

class ReplayRobot : ScreenRobot<ReplayRobot>() {

    fun clickPickFileButton(): ReplayRobot {
        return clickView(R.id.pick_file_button)
    }

    fun chooseWifi(): ReplayRobot {
        return clickView(Type.WIFI.toString())
    }

    fun chooseBt(): ReplayRobot {
        return clickView(Type.BLUETOOTH.toString())
    }

    fun confirmDataType(): ReplayRobot {
        return clickView(R.id.md_buttonDefaultPositive)
    }

    fun hasFilePickerCorrectTitle(): ReplayRobot {
        return checkViewHasText(R.id.title, R.string.file_picker_title)
    }
}
