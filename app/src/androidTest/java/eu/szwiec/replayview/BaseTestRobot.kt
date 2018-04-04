package eu.szwiec.replayview

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.anything

open class BaseTestRobot {

    fun checkViewHasText(@IdRes viewId: Int, @StringRes stringId: Int): ViewInteraction =
            onView(withId(viewId))
                    .check(matches(withText(stringId)))

    fun clickView(@IdRes viewId: Int): ViewInteraction =
            onView(withId(viewId)).perform(click())

    fun clickView(text: String): ViewInteraction =
            onView(withText(text)).perform(click())


    fun clickListItem(position: Int): ViewInteraction =
            onData(anything())
                    .inAdapterView(withId(R.id.fileList))
                    .atPosition(position).perform(click());

}