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
        val speeds = activityRule.activity.resources.getIntArray(R.array.replaySpeeds)
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
                .isSpeedCorrect(speeds[0])
                .clickSpeedButton()
                .isSpeedCorrect(speeds[1])
                .clickSpeedButton()
                .isSpeedCorrect(speeds[2])
                .clickSpeedButton()
                .isSpeedCorrect(speeds[3])
                .clickSpeedButton()
                .isSpeedCorrect(speeds[0])

        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}