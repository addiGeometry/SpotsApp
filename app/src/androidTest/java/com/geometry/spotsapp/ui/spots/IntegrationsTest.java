package com.geometry.spotsapp.ui.spots;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import android.view.View;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import static org.hamcrest.Matchers.allOf;

import com.geometry.spotsapp.R;
import com.geometry.spotsapp.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class IntegrationsTest {
    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void init(){
        activityActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void isNavigationBarInitialized(){
        onView(withId(R.id.navigation_share)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_map)).check(matches(isDisplayed()));
       onView(withId(R.id.navigation_spots)).check(matches(isDisplayed()));
    }

    @Test
    public void existtiertDerAddCategoryButton(){
        onView(withId(R.id.navigation_spots)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.addCategoryButton)).check(matches(isDisplayed()));
    }

    @Test
    public void addCategoryDialogPositiv(){
        onView(withId(R.id.navigation_spots)).perform(click());
        onView(withId(R.id.addCategoryButton)).perform(click());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.add_cat_dialog)).check(matches(isDisplayed()));
    }

    @Test
    public void expandListView(){
        onView(withId(R.id.navigation_spots)).perform(click());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Sport")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.expandedListItem)).check(matches(isDisplayed()));
    }

    private String testkategorie="Test Kategorie";

    @Test
    public void erzeugeTestKategorie(){
        onView(withId(R.id.navigation_spots)).perform(click());
        onView(withId(R.id.addCategoryButton)).perform(click());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.category_name)).perform(replaceText(testkategorie));
        onView(withId(R.id.category_ok)).perform(click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loescheTestKategorieWieder(){
        erzeugeTestKategorie();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything()).inAdapterView(withId(R.id.categoryTree))
                 .onChildView(withText(testkategorie)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private int xkoord = 500;
    private int ykoord = 500;
    @Test
    public void checkCategorySpinnerInMap(){
        erzeugeTestKategorie();
        //onView(withId(R.id.navigation_spots)).perform(click());
        onView(withId(R.id.navigation_map)).perform(click());


        onView(withId(R.id.map_view)).perform(clickXY(xkoord,ykoord));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.categorySpinner)).perform(click());
        onView(withId(R.id.categorySpinner))
                .check(matches(withSpinnerText(containsString(testkategorie))));
    }
    private String testSpot ="Test";
    @Test
    public void erzeugeSpot(){
        onView(withId(R.id.navigation_spots)).perform(click());
        onView(withId(R.id.navigation_map)).perform(click());

        onView(withId(R.id.map_view)).perform(clickXY(xkoord,ykoord));
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.bookmark_title)).perform(replaceText(testSpot));
        onView(withId(R.id.bookmark_ok));
    }

    @Test
    public void erzeugeSpotOhneNamenNegativtest(){
    }

    @Test
    public void erzeugeSpotUndLoescheIhn(){
    }

    public static ViewAction clickXY(final int x, final int y){
        return new GeneralClickAction(
                Tap.LONG,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }
}