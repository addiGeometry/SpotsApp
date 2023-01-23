package com.geometry.spotsapp.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.rule.ActivityTestRule;

import com.geometry.spotsapp.R;
import com.geometry.spotsapp.ui.MainActivity;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ViewTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void ensureTextChangesWork() {
        // Type text and then press the button.
        onView(withId(R.id.nav_host_fragment_activity_main));
    }
}
