package eu.szwiec.replayview

import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun dialogsShowCorrectly() {
        ReplayRobot()
                .clickPickFileButton()
                .chooseBt()
                .chooseWifi()
                .confirmDataType()
                .hasFilePickerCorrectTitle()
    }
}