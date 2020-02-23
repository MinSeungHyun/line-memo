package com.seunghyun.linememo.ui.list


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.seunghyun.linememo.R
import com.seunghyun.linememo.data.AppDatabase.Companion.DB_NAME
import com.seunghyun.linememo.utils.childAtPosition
import org.hamcrest.Matchers.allOf
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TITLE = "Deletion test"

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class DeleteMemoTest {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ListActivity::class.java)

    companion object {
        @BeforeClass
        @JvmStatic
        fun clearAppData() {
            InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase(DB_NAME)
        }
    }

    @Test
    fun deleteMemoTest() {
        val floatingActionButton = onView(
            allOf(withId(R.id.addButton),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.swipeRefreshLayout),
                        0),
                    0),
                isDisplayed()))
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

        val overflowMenuButton = onView(
            allOf(childAtPosition(
                childAtPosition(
                    withId(R.id.action_bar),
                    1),
                1),
                isDisplayed()))
        overflowMenuButton.perform(click())

        val appCompatTextView = onView(
            allOf(withId(R.id.title), withText(R.string.delete),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.content),
                        0),
                    0),
                isDisplayed()))
        appCompatTextView.perform(click())

        val textView = onView(allOf(withText(R.string.no_memos)))
        textView.check(matches(isDisplayed()))
    }
}
