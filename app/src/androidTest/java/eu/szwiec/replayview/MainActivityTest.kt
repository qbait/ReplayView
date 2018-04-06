package eu.szwiec.replayview

import android.support.test.espresso.IdlingRegistry
import android.support.test.rule.ActivityTestRule
import eu.szwiec.replayview.replay.SPEEDS
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
                .isSpeedCorrect(SPEEDS[0])
                .clickSpeedButton()
                .isSpeedCorrect(SPEEDS[1])
                .clickSpeedButton()
                .isSpeedCorrect(SPEEDS[2])
                .clickSpeedButton()
                .isSpeedCorrect(SPEEDS[3])
                .clickSpeedButton()
                .isSpeedCorrect(SPEEDS[0])

        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}