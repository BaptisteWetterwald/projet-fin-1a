package fr.ensisa.ensiblog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityTestRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testTextFieldInput() {

        onView(withId(R.id.editTextLoginUsername)).perform(clearText());
        onView(withId(R.id.editTextLoginUsername)).perform(replaceText("arthur.sicard@uha.fr"));

        onView(withId(R.id.editTextLoginPassword)).perform(clearText());
        onView(withId(R.id.editTextLoginPassword)).perform(replaceText("1234Azerty_"));

        onView(withId(R.id.buttonLogin)).perform(click());

        ActivityScenario.launch(LoginActivity.class);

        // Verify that the expected activity is launched
        intended(hasComponent(MainActivity.class.getName()));
    }
}
