package com.seunghyun.linememo.ui


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.seunghyun.linememo.R
import com.seunghyun.linememo.ui.list.ListActivity
import com.seunghyun.linememo.utils.EXAMPLE_IMAGE_URL
import com.seunghyun.linememo.utils.childAtPosition
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val FIRST_TITLE = "First title"
private const val FIRST_CONTENT = "First content"
private const val SECOND_TITLE = "Second title"
private const val SECOND_CONTENT = "Second content"

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class EditMemoTest {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ListActivity::class.java)

    @Test
    fun editMemoTest() {
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
        appCompatEditText.perform(replaceText(FIRST_TITLE), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(withId(R.id.contentEditText),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    2),
                isDisplayed()))
        appCompatEditText2.perform(replaceText(FIRST_CONTENT), closeSoftKeyboard())

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

        val appCompatEditText4 = onView(
            allOf(withId(R.id.linkEditText),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.custom),
                        0),
                    0),
                isDisplayed()))
        appCompatEditText4.perform(replaceText(EXAMPLE_IMAGE_URL), closeSoftKeyboard())

        val appCompatButton = onView(withText(R.string.ok))
        appCompatButton.perform(click())

        val actionMenuItemView = onView(withId(R.id.saveButton))
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
        textView.check(matches(withText(FIRST_TITLE)))

        val textView2 = onView(allOf(withId(R.id.contentText)))
        textView2.check(matches(withText(FIRST_CONTENT)))

        val imageView = onView(
            allOf(withId(R.id.imageView), withContentDescription(R.string.picture),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.imagesRecyclerView),
                        0),
                    0),
                isDisplayed()))
        imageView.check(matches(isDisplayed()))

        val actionMenuItemView2 = onView(withId(R.id.editButton))
        actionMenuItemView2.perform(click())

        val appCompatEditText5 = onView(
            allOf(withId(R.id.titleEditText), withText(FIRST_TITLE),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    0),
                isDisplayed()))
        appCompatEditText5.perform(replaceText(SECOND_TITLE))

        val appCompatEditText6 = onView(
            allOf(withId(R.id.titleEditText), withText(SECOND_TITLE),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    0),
                isDisplayed()))
        appCompatEditText6.perform(closeSoftKeyboard())

        val appCompatEditText7 = onView(
            allOf(withId(R.id.contentEditText), withText(FIRST_CONTENT),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    2),
                isDisplayed()))
        appCompatEditText7.perform(replaceText(SECOND_CONTENT))

        val appCompatEditText8 = onView(
            allOf(withId(R.id.contentEditText), withText(SECOND_CONTENT),
                childAtPosition(
                    allOf(withId(R.id.rootView),
                        childAtPosition(
                            withId(android.R.id.content),
                            0)),
                    2),
                isDisplayed()))
        appCompatEditText8.perform(closeSoftKeyboard())

        val appCompatImageView = onView(
            allOf(withContentDescription(R.string.delete),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.imagesRecyclerView),
                        0),
                    1),
                isDisplayed()))
        appCompatImageView.perform(click())

        val actionMenuItemView3 = onView(withId(R.id.saveButton))
        actionMenuItemView3.perform(click())

        Thread.sleep(500)
        val view2 = onView(
            allOf(childAtPosition(
                childAtPosition(
                    withId(R.id.memoRecyclerView),
                    0),
                3),
                isDisplayed()))
        view2.perform(click())

        val textView3 = onView(withId(R.id.titleText))
        textView3.check(matches(withText(SECOND_TITLE)))

        val textView4 = onView(withId(R.id.contentText))
        textView4.check(matches(withText(SECOND_CONTENT)))

        val recyclerView = onView(withId(R.id.imagesRecyclerView))
        recyclerView.check(matches(hasChildCount(0)))
    }
}
