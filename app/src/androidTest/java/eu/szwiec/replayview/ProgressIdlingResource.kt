package eu.szwiec.replayview

import android.support.test.espresso.IdlingResource
import android.support.test.rule.ActivityTestRule
import android.view.View

class ProgressIdlingResource(private val activityRule: ActivityTestRule<MainActivity>): IdlingResource {
    override fun getName(): String {
        return ProgressIdlingResource::class.java.name
    }

    override fun isIdleNow(): Boolean {

        val progress = activityRule.activity.findViewById<View>(android.R.id.progress)
        val idle = progress == null
        if (idle) {
            callback?.onTransitionToIdle()
        }
        return idle
    }

    private var callback: IdlingResource.ResourceCallback? = null

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}