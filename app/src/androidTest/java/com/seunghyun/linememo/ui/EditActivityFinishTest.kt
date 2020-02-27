package com.seunghyun.linememo.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.seunghyun.linememo.R
import com.seunghyun.linememo.ui.list.ListActivity
import com.seunghyun.linememo.utils.childAtPosition
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TITLE = "Title"
private const val CONTENT = "Content"

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class EditActivityFinishTest {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ListActivity::class.java)

    @Test
    fun editActivityFinishTest() {
        val floatingActionButton = onView(withId(R.id.addButton))
        floatingActionButton.perform(click())
        pressBack()
        floatingActionButton.perform(click())

        val appCompatEditText2 = onView(
            allOf(withId(R.id.contentEditText),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    2),
                isDisplayed()))
        appCompatEditText2.perform(replaceText(CONTENT), closeSoftKeyboard())

        pressBack()

        val textView = onView(
            allOf(IsInstanceOf.instanceOf(android.widget.TextView::class.java), withText(R.string.save_question)))
        textView.check(matches(withText(R.string.save_question)))

        val appCompatButton = onView(allOf(withId(android.R.id.button1), withText(R.string.save)))
            .inRoot(RootMatchers.isDialog())
        appCompatButton.perform(scrollTo(), click())

        val appCompatEditText3 = onView(
            allOf(withId(R.id.titleEditText),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    0),
                isDisplayed()))
        appCompatEditText3.perform(replaceText(TITLE), closeSoftKeyboard())

        val appCompatImageButton = onView(
            allOf(
                childAtPosition(
                    allOf(withId(R.id.action_bar),
                        childAtPosition(
                            withId(R.id.action_bar_container),
                            0)),
                    1),
                isDisplayed()))
        appCompatImageButton.perform(click())

        val appCompatButton2 = onView(allOf(withId(android.R.id.button1), withText(R.string.save)))
        appCompatButton2.perform(scrollTo(), click())

        val view = onView(
            allOf(childAtPosition(
                childAtPosition(
                    withId(R.id.memoRecyclerView),
                    0),
                3),
                isDisplayed()))
        view.perform(click())

        val appCompatImageButton2 = onView(
            allOf(
                childAtPosition(
                    allOf(withId(R.id.action_bar),
                        childAtPosition(
                            withId(R.id.action_bar_container),
                            0)),
                    1),
                isDisplayed()))
        appCompatImageButton2.perform(click())

        val imageButton = onView(withId(R.id.addButton))
        imageButton.check(matches(isDisplayed()))
    }
}
