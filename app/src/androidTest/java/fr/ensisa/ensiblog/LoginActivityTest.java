package fr.ensisa.ensiblog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static java.lang.Thread.sleep;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.ensisa.ensiblog.firebase.Authentication;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {


    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        Intents.init();
        ActivityScenario.launch(LoginActivity.class);
        Thread.sleep(500);
    }


    @After
    public void tearDown() throws InterruptedException {
        Intents.release();
        Thread.sleep(500);
    }

    @Test
    public void testGoRegister() throws InterruptedException {
        // Protocol to test
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.buttonRegister)).perform(click());

        // Verify that the expected login page is launched
        Thread.sleep(1000);
        intended(IntentMatchers.hasComponent(RegisterActivity.class.getName()));
    }

    @Test
    public void testTextFieldInputValide() throws InterruptedException {
        // Protocol to test
        onView(withId(R.id.editTextLoginUsername)).perform(clearText());
        onView(withId(R.id.editTextLoginUsername)).perform(replaceText("arthur.sicard@uha.fr"));
        onView(withId(R.id.editTextLoginPassword)).perform(clearText());
        onView(withId(R.id.editTextLoginPassword)).perform(replaceText("1234Azerty_"));
        onView(withId(R.id.buttonLogin)).perform(click());

        // Verify that the expected activity is launched
        Thread.sleep(1000);
        intended(IntentMatchers.hasComponent(MainActivity.class.getName()));

        // clean test
        Thread.sleep(500);
        new Authentication().signOut();
    }


    @Test
    public void testTextFieldInputInvalideMail() throws InterruptedException {
        // Protocol to test
        onView(withId(R.id.editTextLoginUsername)).perform(clearText());
        onView(withId(R.id.editTextLoginUsername)).perform(replaceText("arthur.sicard@gmail.com"));
        onView(withId(R.id.editTextLoginPassword)).perform(clearText());
        onView(withId(R.id.editTextLoginPassword)).perform(replaceText("1234Azerty_"));
        onView(withId(R.id.buttonLogin)).perform(click());

        // Verify that the expected activity is launched
        sleep(1000);
        intended(not(hasComponent(MainActivity.class.getName())));
    }

    @Test
    public void testTextFieldInputInvalideMDP() throws InterruptedException {
        // Protocol to test
        onView(withId(R.id.editTextLoginUsername)).perform(clearText());
        onView(withId(R.id.editTextLoginUsername)).perform(replaceText("arthur.sicard@uha.fr"));
        onView(withId(R.id.editTextLoginPassword)).perform(clearText());
        onView(withId(R.id.editTextLoginPassword)).perform(replaceText("azerty"));
        onView(withId(R.id.buttonLogin)).perform(click());

        // Verify that the expected activity is launched
        sleep(1000);
        intended(not(hasComponent(MainActivity.class.getName())));
    }
}
