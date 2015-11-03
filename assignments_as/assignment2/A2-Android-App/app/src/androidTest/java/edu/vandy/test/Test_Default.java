package edu.vandy.test;

import com.robotium.solo.Solo;

import edu.vandy.view.GazingSimulationActivity;
import junit.framework.Assert;
import edu.vandy.view.PalantiriActivity;

public class Test_Default extends Test_for_Palantiri {
    public void testRun() {
        solo.clickOnView(solo.getView(edu.vandy.R.id.button_simulation));
        solo.sleep(shortDelay);
		
        Assert.assertTrue("Test failed: GazingSimulationActivity did not load",
                          solo.waitForActivity(GazingSimulationActivity.class));
	    
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.sleep(shortDelay);
	    
        Assert.assertTrue("Test failed: GazingSimulationActivity did not load",
                          solo.waitForActivity(GazingSimulationActivity.class));
	    
        // Rotate the screen back to portrait.
        solo.setActivityOrientation(Solo.PORTRAIT);

        // Give the rotation time to settle.
        solo.sleep(shortDelay);

        // Wait for activity
        Assert.assertTrue("Test failed: GazingSimulationActivity did not",
                          solo.waitForActivity(GazingSimulationActivity.class));
        solo.clickOnView(solo.getView(edu.vandy.R.id.button_simulation));
	    
        // Wait for activity
        Assert.assertTrue("Test failed: Unable to Stop Simulation",
                          solo.waitForText(" simulation threads are being halted"));
		

        solo.clickOnView(solo.getView(edu.vandy.R.id.button_simulation));

        solo.goBackToActivity("PalantiriActivity");
        
        Assert.assertTrue("Test failed: PalantiriActivity did not load"
                          + " after returning from GazingSimulationActivity",
                          solo.waitForActivity(PalantiriActivity.class));
    }
}
