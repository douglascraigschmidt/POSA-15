package edu.vandy.test;

import com.robotium.solo.Solo;
import edu.vandy.view.PalantiriActivity;
import junit.framework.Assert;

public class Test_Rotations extends Test_for_Palantiri {
    public void testRun() {
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.sleep(shortDelay);
	    
        Assert.assertTrue("Test failed: Rotation Failed",
                          solo.waitForActivity(PalantiriActivity.class));
	    
        // Rotate the screen back to portrait.
        solo.setActivityOrientation(Solo.PORTRAIT);

        // Give the rotation time to settle.
        solo.sleep(shortDelay);

        // Wait for activity
        Assert.assertTrue("Test failed: Rotation Failed",
                          solo.waitForActivity(PalantiriActivity.class));
    }
}
