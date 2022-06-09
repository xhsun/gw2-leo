package me.xhsun.gw2leo.account.ui

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.MainActivity
import me.xhsun.gw2leo.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    private val faker = Faker()


    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testInputValidAPI(): Unit = runBlocking {
        val input = faker.random.nextUUID()
        val intent = Intent(ApplicationProvider.getApplicationContext(), LoginActivity::class.java)
        val scenario = ActivityScenario.launch<LoginActivity>(intent)
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withId(R.id.login_input_edit_text)).perform(typeText(input), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())

        delay(1000)
        intended(hasComponent(MainActivity::class.java.name))
        scenario.close()
    }
}