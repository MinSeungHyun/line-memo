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
import com.seunghyun.linememo.utils.EXAMPLE_IMAGE_URL
import com.seunghyun.linememo.utils.childAtPosition
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TITLE = "Title"

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class ImageAdditionDeletionTest {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ListActivity::class.java)

    @Test
    fun imageAdditionDeletionTest() {
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

        val appCompatEditText3 = onView(withId(R.id.linkEditText))
        appCompatEditText3.perform(replaceText(EXAMPLE_IMAGE_URL), closeSoftKeyboard())

        val appCompatButton = onView(withText(R.string.ok))
        appCompatButton.perform(click())

        val appCompatImageView = onView(
            allOf(withContentDescription(R.string.delete),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.imagesRecyclerView),
                        0),
                    1),
                isDisplayed()))
        appCompatImageView.perform(click())

        val appCompatTextView3 = onView(
            allOf(withId(R.id.addImageButton), withText(R.string.add_images),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    6),
                isDisplayed()))
        appCompatTextView3.perform(click())

        appCompatTextView2.perform(click())

        val appCompatEditText5 = onView(
            allOf(withId(R.id.linkEditText),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.custom),
                        0),
                    0),
                isDisplayed()))
        appCompatEditText5.perform(replaceText(EXAMPLE_IMAGE_URL), closeSoftKeyboard())

        val appCompatButton2 = onView(withId(android.R.id.button1))
        appCompatButton2.perform(click())

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

        val imageView = onView(
            allOf(withId(R.id.imageView), withContentDescription(R.string.picture),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.imagesRecyclerView),
                        0),
                    0),
                isDisplayed()))
        imageView.check(matches(isDisplayed()))
    }

    @After
    fun finish() {
        mActivityTestRule.finishActivity()
    }
}
