package at.tugraz.xp10;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static at.tugraz.xp10.Utils.EditTextMatchers.withErrorText;


@RunWith(AndroidJUnit4.class)
public class LoginInstrumentedTest {

    @Rule
    public ActivityTestRule<LoginActivity> menuActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void init() throws InterruptedException {
        closeSoftKeyboard();
        // sign out before tests
        if(menuActivityTestRule.getActivity().isUserLoggedIn())
        {
            // need to sign out
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
            onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));
            Thread.sleep(4000);
        }
    }

    @Test
    public void displayLayout() throws Exception {
        onView(withId(R.id.email)).check(matches(isDisplayed()));
        onView(withId(R.id.password)).check(matches(isDisplayed()));
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
        onView(withId(R.id.forgot_password_button)).check(matches(isDisplayed()));
        onView(withId(R.id.register_button)).check(matches(isDisplayed()));
    }
    @Test
    public void pressLoginButtonMailRequired() throws Exception {
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.email)).check(matches(withErrorText(R.string.error_field_required)));
    }
    @Test
    public void pressLoginButtonMailWrong() throws Exception {
        onView(withId(R.id.email)).perform(typeText("wrong"));
        onView(withId(R.id.login_button)).perform(scrollTo()).perform(click());
        onView(withId(R.id.email)).check(matches(withErrorText(R.string.error_invalid_email)));
    }
    @Test
    public void pressLoginButtonPasswordRequired() throws Exception {
        onView(withId(R.id.login_button)).perform(scrollTo()).perform(click());
        onView(withId(R.id.password)).check(matches(withErrorText(R.string.error_field_required)));
    }
    @Test
    public void pressLoginButtonPasswordTooShort() throws Exception {
        onView(withId(R.id.password)).perform(typeText("123"));
        onView(withId(R.id.login_button)).perform(scrollTo()).perform(click());
        onView(withId(R.id.password)).check(matches(withErrorText(R.string.error_invalid_password)));
    }
    @Test
    public void pressLoginButtonEmpty() throws Exception {
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.email)).check(matches(withErrorText(R.string.error_field_required)));
        onView(withId(R.id.password)).check(matches(withErrorText(R.string.error_field_required)));
    }
    @Test
    public void pressLoginButtonCorrect() throws Exception {
        onView(withId(R.id.email)).perform(typeText("ui@test.com"));
        onView(withId(R.id.password)).perform(typeText("test1234"));
        closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(scrollTo()).perform(click());

    }

    @Test
    public void pressRegisterButton() throws Exception {
        closeSoftKeyboard();
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.register_fragment)).check(matches(isDisplayed()));
    }
    @Test
    public void pressForgotPasswordButton() throws Exception {
        closeSoftKeyboard();
        onView(withId(R.id.forgot_password_button)).perform(click());
        onView(withId(R.id.forgot_password_fragment)).check(matches(isDisplayed()));
    }
}
