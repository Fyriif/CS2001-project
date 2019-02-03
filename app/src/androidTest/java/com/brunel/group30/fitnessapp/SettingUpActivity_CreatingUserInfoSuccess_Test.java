package com.brunel.group30.fitnessapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingUpActivity_CreatingUserInfoSuccess_Test {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void settingUpActivity_CreatingUserInfoSuccess_Test() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        FirebaseFirestore firebaseDatabase = FirebaseFirestore.getInstance();
        String userUid = "QRdjRmzHT1fsOOFBkIg65Xc8uAo1";

        firebaseDatabase.collection(DBFields.USER_INFO_COLLECTION).document(userUid)
                .delete()
                .addOnFailureListener(e -> fail("Unable to delete collection"));

        String testEmail = "test@test.com";

        ViewInteraction appCompatEmailEditTextClick = onView(
                allOf(withId(R.id.edit_email),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEmailEditTextClick.perform(click());

        ViewInteraction appCompatEmailEditTextType = onView(
                allOf(withId(R.id.edit_email),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEmailEditTextType.perform(replaceText(testEmail), closeSoftKeyboard());

        ViewInteraction appCompatPasswordEditTextClick = onView(
                allOf(withId(R.id.edit_password),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatPasswordEditTextClick.perform(click());

        ViewInteraction appCompatPasswordEditTextType = onView(
                allOf(withId(R.id.edit_password),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatPasswordEditTextType.perform(replaceText("adminAdmin123#"), closeSoftKeyboard());

        ViewInteraction appCompatLoginButtonClick = onView(
                allOf(withId(R.id.button_login),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatLoginButtonClick.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatForenameEditText = onView(
                allOf(withId(R.id.edit_text_forename),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatForenameEditText.perform(replaceText("John"), closeSoftKeyboard());

        ViewInteraction appCompatSurnameEditText = onView(
                allOf(withId(R.id.edit_text_surname),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatSurnameEditText.perform(replaceText("Dailey"), closeSoftKeyboard());

        ViewInteraction appCompatDateOfBirthEditText = onView(
                allOf(withId(R.id.edit_text_dob),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatDateOfBirthEditText.perform(click());

        ViewInteraction appCompatDateOfBirthConfirmButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatDateOfBirthConfirmButton.perform(click());

        ViewInteraction appCompatNextButton = onView(
                allOf(withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatNextButton.perform(click());

        ViewInteraction appCompatSexRadioButton = onView(
                allOf(withId(R.id.button_sex_male), withText("Male"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatSexRadioButton.perform(click());

        appCompatNextButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatNextButton.perform(click());

        appCompatNextButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatNextButton.perform(click());

        appCompatNextButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatNextButton.perform(click());

        ViewInteraction appCompatDisabilityRadioButton = onView(
                allOf(withId(R.id.button_disability_no),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                1),
                        isDisplayed()));
        appCompatDisabilityRadioButton.perform(click());

        appCompatNextButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatNextButton.perform(click());

        ViewInteraction appCompatLocationCheckBox = onView(
                allOf(withId(R.id.button_home), withText("Home"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3),
                                1),
                        isDisplayed()));
        appCompatLocationCheckBox.perform(click());

        appCompatNextButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatNextButton.perform(click());

        ViewInteraction appCompatCheckBox2 = onView(
                allOf(withId(R.id.button_day_saturday), withText("Saturday"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        2),
                                1),
                        isDisplayed()));
        appCompatCheckBox2.perform(click());

        appCompatNextButton = onView(
                allOf(withId(R.id.btnPositiveDialog),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatNextButton.perform(click());

        appCompatNextButton = onView(
                allOf(withId(R.id.button_submit),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatNextButton.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}