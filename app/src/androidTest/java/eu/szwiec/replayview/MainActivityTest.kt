package eu.szwiec.replayview

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.IdlingRegistry
import android.support.test.rule.ActivityTestRule
import eu.szwiec.replayview.replay.ReplayFile
import eu.szwiec.replayview.replay.Type
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

        val idlingResource = ProgressIdlingResource(activityRule)
        IdlingRegistry.getInstance().register(idlingResource)

        robot
                .clickPickFileButton()
                .chooseBt()
                .chooseWifi()
                .confirmType()
                .hasFilePickerCorrectTitle()
                .confirmFile()
                .isTotalTimeCorrect()

        IdlingRegistry.getInstance().unregister(idlingResource)

    }
}