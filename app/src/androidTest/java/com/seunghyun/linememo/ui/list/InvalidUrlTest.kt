package com.seunghyun.linememo.ui.list

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.seunghyun.linememo.R
import com.seunghyun.linememo.utils.childAtPosition
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TITLE = "Title"
private const val TESTING_ERROR_URL = "https://"

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class InvalidUrlTest {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ListActivity::class.java)

    @Test
    fun invalidUrlTest() {
        val floatingActionButton = onView(withId(R.id.addButton))
        floatingActionButton.perform(click())

        val appCompatEditText = onView(
            allOf(withId(R.id.titleEditText),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    0),
                isDisplayed()))
        appCompatEditText.perform(replaceText(TITLE), closeSoftKeyboard())

        val appCompatTextView = onView(
            allOf(withId(R.id.addImageButton), withText(R.string.add_images),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    6),
                isDisplayed()))
        appCompatTextView.perform(click())

        val appCompatTextView2 = onView(withText(R.string.link))
            .inRoot(RootMatchers.isPlatformPopup())
        appCompatTextView2.perform(click())

        val appCompatEditText2 = onView(withId(R.id.linkEditText))
        appCompatEditText2.perform(replaceText(TESTING_ERROR_URL), closeSoftKeyboard())

        val appCompatButton = onView(withText(R.string.ok))
        appCompatButton.perform(click())

        Thread.sleep(2000)
        val recyclerView = onView(withId(R.id.imagesRecyclerView))
        recyclerView.check(matches(hasChildCount(0)))
    }
}