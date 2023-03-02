package com.example.hinvas

import androidx.test.runner.AndroidJUnit4;
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Before
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun Test_Hello(){
        onView(allOf(withId(R.id.result_txt), withText("Hello World!"))).check(matches(isDisplayed()))

    }

    @Test
    fun Test_Youtube(){
        val  youtubeBtn= onView(withId(R.id.youtubeAPI_test_btn))
        val txt= onView(withId(R.id.result_txt))
        youtubeBtn.perform(click())
        txt.check(matches(withText("エラー")))
        youtubeBtn.perform(click())
        txt.check(matches(withText("エラー")))
    }

}
