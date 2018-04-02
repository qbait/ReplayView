package eu.szwiec.replayview;


import eu.szwiec.replayview.replay.ImportDataManager;

public class ReplayRobot extends ScreenRobot<ReplayRobot> {

    public ReplayRobot clickPickFileButton() {
        return clickView(R.id.pick_file_button);
    }

    public ReplayRobot chooseWifi() {
        return clickView(ImportDataManager.TYPE_WIFI);
    }

    public ReplayRobot chooseBt() {
        return clickView(ImportDataManager.TYPE_BLUETOOTH);
    }

    public ReplayRobot confirmDataType() {
        return clickView(R.id.md_buttonDefaultPositive);
    }

    public ReplayRobot hasFilePickerCorrectTitle() {
        return checkViewHasText(R.id.title, R.string.file_picker_title);
    }
}
