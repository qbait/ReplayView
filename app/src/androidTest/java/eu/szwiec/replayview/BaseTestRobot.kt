package eu.szwiec.replayview

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.not


open class BaseTestRobot {

    fun checkViewHasText(@IdRes viewId: Int, @StringRes stringId: Int): ViewInteraction =
            onView(withId(viewId))
                    .check(matches(withText(stringId)))

    fun checkViewHasText(@IdRes viewId: Int, string: String): ViewInteraction =
            onView(withId(viewId))
                    .check(matches(withText(string)))

    fun clickView(@IdRes viewId: Int): ViewInteraction =
            onView(withId(viewId)).perform(click())

    fun clickView(text: String): ViewInteraction =
            onView(withText(text)).perform(click())

    fun isViewEnabled(@IdRes viewId: Int): ViewInteraction =
            onView(withId(viewId)).check(matches(isEnabled()))

    fun isViewDisabled(@IdRes viewId: Int): ViewInteraction =
            onView(withId(viewId)).check(matches(not(isEnabled())))

    fun isViewChecked(@IdRes viewId: Int): ViewInteraction =
            onView(withId(viewId)).check(matches(isChecked()))

    fun isViewUnchecked(@IdRes viewId: Int): ViewInteraction =
            onView(withId(viewId)).check(matches(not(isChecked())))

}