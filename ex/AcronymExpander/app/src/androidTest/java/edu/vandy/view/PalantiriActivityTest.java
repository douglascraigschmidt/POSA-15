package edu.vandy.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.vandy.R;
import edu.vandy.common.Toaster;
import edu.vandy.common.Utils;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class PalantiriActivityTest {
    /**
     * Logging tag.
     */
    private static final String TAG = "PalantiriActivityTest";

    /**
     * Wait time constants.
     */
    private final int CONFIG_TIMEOUT = 4000;
    private final int SHUTDOWN_TIMEOUT = 6000;

    /**
     * Input values.
     */
    private final int PALANTIRI = 4;
    private final int BEINGS = 6;
    private final int ITERATIONS = 5;

    @Rule
    public ActivityTestRule<PalantiriActivity> activityTestRule =
            new ActivityTestRule<>(PalantiriActivity.class);

    @Test
    public void palantiriActivityTest() {
        // Create and install a mock Toaster implementation.
        MockToaster mockToaster = new MockToaster();
        Utils.setMockToaster(mockToaster);

        // Force config change.
        setOrientationPortrait(CONFIG_TIMEOUT);

        // Setup start and stop button view matchers which are used
        // frequently throughout this test.
        ViewInteraction startButton = onView(
                allOf(withId(R.id.button_simulation),
                      withText(R.string.button_start_simulation),
                      isDisplayed()));

        ViewInteraction stopButton = onView(
                allOf(withId(R.id.button_simulation),
                      withText(R.string.button_stop_simulation),
                      isDisplayed()));

        //
        // Create a list of widget resource id / input value pairs
        // to populate the starting activity values.
        List<Pair<Integer, Integer>> pairs =
                Stream.of(
                        Pair.create(R.id.edittext_number_of_palantiri,
                                    PALANTIRI),
                        Pair.create(R.id.edittext_number_of_beings, BEINGS),
                        Pair.create(R.id.edittext_gazing_iterations, ITERATIONS)
                ).collect(Collectors.toList());

        // Populate all the EditViews.
        pairs.stream().forEach(
                p -> onView(withId(p.first)).perform(
                        typeText(p.second.toString())));

        // Check for expected values.
        pairs.stream().forEach(
                p -> onView(withId(p.first)).check(
                        matches(withText(
                                p.second.toString()))));

        // Force a config change.
        setOrientationLandscape(CONFIG_TIMEOUT);

        // Make sure the fields still have the correct values.
        pairs.stream().forEach(
                p -> onView(withId(p.first)).check(
                        matches(withText(
                                p.second.toString()))));

        // Start the simulation.
        startButton.perform(click());

        // Force a config change.
        setOrientationLandscape(CONFIG_TIMEOUT);

        // Check for a toast message that notifies the user that
        // the simulation is being resumed after a config change
        // (a timeout was already specified in the orientation call)
        Assert.assertTrue(mockToaster.hasAnyMessageStartingWith(
                R.string.toast_simulation_resume, 0));

        // Force simulation to stop.
        stopButton.perform(click());

        // Check for expected stop simulation toast (should be immediate).
        Assert.assertTrue(
                mockToaster.hasAnyMessage(
                        String.format(activityTestRule.getActivity().getString(
                                R.string.toast_simulation_stopped, BEINGS),
                                      0)));

        // Now check for the toast that is displayed once the shutdown
        // sequence has completed. Note that this can take a few seconds
        // so we specify SHUTDOWN_TIMEOUT as the waiting period.
        Assert.assertTrue(mockToaster.hasAnyMessageStartingWith(
                R.string.toast_simulation_complete, SHUTDOWN_TIMEOUT));

        // Clear the mock toast messages for the next test.
        mockToaster.clear();

        // Start a new simulation.
        startButton.perform(click());

        // Force a config change.
        setOrientationPortrait(CONFIG_TIMEOUT);

        // Check for a toast message that notifies the user that
        // the simulation is being resumed after a config change
        // (a timeout was already specified in the orientation call)
        Assert.assertTrue(mockToaster.hasAnyMessageStartingWith(
                R.string.toast_simulation_resume, 0));

        // Force simulation to stop.
        stopButton.perform(click());

        // Check for expected stop simulation toast (should be immediate).
        Assert.assertTrue(
                mockToaster.hasAnyMessage(
                        String.format(activityTestRule.getActivity().getString(
                                R.string.toast_simulation_stopped, BEINGS),
                                      0)));

        // Now check for the toast that is displayed once the shutdown
        // sequence has completed. Note that this can take a few seconds
        // so we specify SHUTDOWN_TIMEOUT as the waiting period.
        Assert.assertTrue(mockToaster.hasAnyMessageStartingWith(
                R.string.toast_simulation_complete, SHUTDOWN_TIMEOUT));

        // Clear the mock toast messages for the next test.
        mockToaster.clear();

        // Force a config change.
        setOrientationLandscape(CONFIG_TIMEOUT);

        // Return to previous activity.
        pressBack();

        // Ensure that the original EditView values are still the same.
        pairs.stream().forEach(
                p -> onView(withId(p.first)).check(
                        matches(withText(p.second.toString()))));

        // Success!
        Log.d(TAG, "The test was successful!");
    }

    public void setOrientationLandscape(int wait) {
        Log.d(TAG, "palantiriActivityTest: setting orientation to LANDSCAPE");
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, wait);
    }

    public void setOrientationPortrait(int wait) {
        Log.d(TAG, "palantiriActivityTest: setting orientation to PORTRAIT");
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, wait);
    }

    public void setOrientation(int orientation, int wait) {
        try {
            getCurrentActivity().setRequestedOrientation(orientation);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        // Give the system app to settle.
        SystemClock.sleep(wait);
    }

    private Activity getCurrentActivity() throws Throwable {
        getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        getInstrumentation().runOnMainSync(() -> {
            java.util.Collection<Activity> activities =
                    ActivityLifecycleMonitorRegistry
                            .getInstance().getActivitiesInStage(Stage.RESUMED);
            activity[0] = Iterables.getOnlyElement(activities);
        });
        return activity[0];
    }

    private class MockToaster implements Toaster {
        /**
         * Default sleep interval used while repeatedly checking for a toast
         * message.
         */
        private static final int WAIT_INTERVAL = 100;

        /**
         * List of toast messages received from the application since the the
         * last clear() operation.
         */
        final ArrayList<String> mMessages = new ArrayList<>();

        /**
         * Mock implementation simply adds passed toast message to an array.
         */
        @Override
        public void showToast(
                Context context, String message, int duration) {
            synchronized (mMessages) {
                mMessages.add(message);
            }
            Toast.makeText(context, message, duration).show();
        }

        /**
         * Returns true if the first and only received toast messages matches
         * the passed message string within the specified time frame.
         */
        boolean hasJustMessage(@StringRes int id, int waitTime) {
            return hasJustMessage(
                    activityTestRule.getActivity().getString(id), waitTime);
        }

        /**
         * Returns true if the first and only received toast messages matches
         * the passed message string within the specified time frame.
         */
        boolean hasJustMessage(String message, int waitTime) {
            do {
                synchronized (mMessages) {
                    if (mMessages.size() > 1) {
                        return false;
                    } else if (mMessages.size() == 1) {
                        return mMessages.contains(message);
                    }
                }
                int sleepTime = Math.min(WAIT_INTERVAL, waitTime);
                SystemClock.sleep(sleepTime);
                waitTime -= sleepTime;
            } while (waitTime >= 0);

            return false;
        }

        /**
         * Returns true if the specified string exactly matches any posted toast
         * messages. Non-matching toast messages that may also be received
         * before or after the expected message.
         */
        boolean hasAnyMessage(@StringRes int id, int waitTime) {
            return hasAnyMessage(
                    activityTestRule.getActivity().getString(id),
                    waitTime);
        }

        /**
         * Returns true if the specified string exactly matches any posted toast
         * messages within the specified wait time. Ignores any additional
         * non-matching toast messages that may also be received before or after
         * the expected message.
         */
        boolean hasAnyMessage(String message, int waitTime) {
            while (waitTime >= 0) {
                synchronized (mMessages) {
                    if (hasAnyMessage(message)) {
                        return true;
                    }
                }
                int sleepTime = Math.min(WAIT_INTERVAL, waitTime);
                SystemClock.sleep(sleepTime);
                waitTime -= sleepTime;
            }
            return false;
        }

        /**
         * Returns true if the specified string has been displayed as a toast
         * message. Ignores any additional non-matching toast messages that may
         * also be received before.or after the expected message.
         */
        boolean hasAnyMessage(String message) {
            synchronized (mMessages) {
                for (String msg : mMessages) {
                    if (msg.equals(message)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Returns true if the specified string resource matches an posted toast
         * message withing the specified wait time. Ignores any additional
         * non-matching toast messages that may also be received before or after
         * the expected message.
         */
        boolean hasAnyMessageStartingWith(@StringRes int id, int waitTime) {
            return hasAnyMessageStartingWith(
                    activityTestRule.getActivity().getString(id),
                    waitTime);
        }

        /**
         * Returns true if the specified string matches an posted toast message
         * withing the specified wait time. Ignores any additional non-matching
         * toast messages that may also be received before or after the expected
         * message.
         */
        boolean hasAnyMessageStartingWith(String message, int waitTime) {
            while (waitTime >= 0) {
                synchronized (mMessages) {
                    if (hasAnyMessageStartingWith(message)) {
                        return true;
                    }
                }
                int sleepTime = Math.min(WAIT_INTERVAL, waitTime);
                SystemClock.sleep(sleepTime);
                waitTime -= sleepTime;
            }
            return false;
        }

        /**
         * Returns true if the specified string has already been posted. Ignores
         * any additional non-matching toast messages that may also be received
         * before or after the expected message.
         */
        boolean hasAnyMessageStartingWith(String message) {
            synchronized (mMessages) {
                for (String msg : mMessages) {
                    if (msg.startsWith(message)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Clears any messages accumulated in the message array.
         */
        void clear() {
            synchronized (mMessages) {
                mMessages.clear();
            }
        }
    }
}
