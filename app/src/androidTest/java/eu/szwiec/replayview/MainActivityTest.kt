package eu.szwiec.replayview

import android.support.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)
    private lateinit var robot: ReplayRobot

    @Before
    fun setup() {
        robot = ReplayRobot(activityRule.activity)
    }

    @Test
    fun dialogsShowCorrectly() {
        robot
                .clickPickFileButton()
                .chooseBt()
                .chooseWifi()
                .confirmDataType()
                .hasFilePickerCorrectTitle()
    }
}