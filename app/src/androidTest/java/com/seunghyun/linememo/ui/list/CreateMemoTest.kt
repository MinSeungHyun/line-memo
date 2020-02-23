package com.seunghyun.linememo.ui.list


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
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
private const val CONTENT = "Content"

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class CreateMemoTest {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ListActivity::class.java)

    @Test
    fun createMemoTest() {
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

        val actionMenuItemView = onView(
            allOf(withId(R.id.saveButton), withContentDescription(R.string.save),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.action_bar),
                        1),
                    0),
                isDisplayed()))
        actionMenuItemView.perform(click())

        val view = onView(
            allOf(childAtPosition(
                childAtPosition(
                    withId(R.id.memoRecyclerView),
                    0),
                3),
                isDisplayed()))
        view.perform(click())

        val textView = onView(allOf(withId(R.id.titleText)))
        textView.check(matches(withText(TITLE)))

        val textView2 = onView(allOf(withId(R.id.contentText)))
        textView2.check(matches(withText(CONTENT)))
    }
}
