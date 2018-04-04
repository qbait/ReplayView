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

        val app = InstrumentationRegistry.getTargetContext().applicationContext as TestApp
        app.setFiles(getSampleFiles())

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

    private fun getSampleFiles(): List<ReplayFile> {
        val assets = activityRule.activity.assets
        val btStream = assets.open("sample.edyuid0")
        val wifiStream = assets.open("sample.wifi0")
        val btFile = ReplayFile(btStream, Type.BLUETOOTH)
        val wifiFile = ReplayFile(wifiStream, Type.WIFI)

        return listOf(btFile, wifiFile)
    }
}