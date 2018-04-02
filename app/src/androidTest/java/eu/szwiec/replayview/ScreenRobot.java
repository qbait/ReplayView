package eu.szwiec.replayview;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public abstract class ScreenRobot<T extends ScreenRobot> {

    public T checkViewHasText(@IdRes int viewId, @StringRes int stringId) {
        onView(withId(viewId))
                .check(matches(withText(stringId)));
        return (T) this;
    }

    public T clickView(@IdRes int viewId) {
        onView(withId(viewId)).perform(click());
        return (T) this;
    }

    public T clickView(String text) {
        onView(withText(text)).perform(click());
        return (T) this;
    }

}
