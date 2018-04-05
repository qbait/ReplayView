package eu.szwiec.replayview

import android.support.test.espresso.IdlingRegistry
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
    fun replayWorksCorrectly() {

        val idlingResource = ProgressIdlingResource(activityRule)
        IdlingRegistry.getInstance().register(idlingResource)

        robot
                .isPlaybackDisabled()
                .clickPickFileButton()
                .chooseBt()
                .chooseWifi()
                .confirmType()
                .hasFilePickerCorrectTitle()
                .confirmFile()
                .isPlaybackEnabled()
                .isPlayingTimeCorrect()
                .isTotalTimeCorrect()
                .isPlayIconShown()
                .play()
                .isPauseIconShown()
                .isSpeedCorrect(1)
                .clickSpeedButton()
                .isSpeedCorrect(4)
                .clickSpeedButton()
                .isSpeedCorrect(16)
                .clickSpeedButton()
                .isSpeedCorrect(32)
                .clickSpeedButton()
                .isSpeedCorrect(1)

        IdlingRegistry.getInstance().unregister(idlingResource)

    }
}