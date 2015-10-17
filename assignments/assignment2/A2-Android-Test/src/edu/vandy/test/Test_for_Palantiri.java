package edu.vandy.test;

import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import edu.vandy.view.GazingSimulationActivity;
import edu.vandy.view.PalantiriActivity;
import junit.framework.Assert;

public class Test_for_Palantiri
       extends ActivityInstrumentationTestCase2<PalantiriActivity> {
    /**
     * Some useful delay/timer values.
     */
    public static int MILLISECOND = 100;
    public static int shortDelay = 2 * MILLISECOND;
    public static int mediumDelay = 5 * MILLISECOND;
    public static int longDelay = 10 * MILLISECOND;
    public static int veryLongDelay = 30 * MILLISECOND;
    protected Solo solo;

    public Test_for_Palantiri() {
        super(PalantiriActivity.class);
    }

    @Override
	protected void setUp()throws Exception {
	super.setUp();
	solo=new Solo(getInstrumentation(),getActivity());
    }

    public void testPalantiriScreen() {
    }

    @Override
    protected void tearDown()throws Exception {
	solo.finishOpenedActivities();
    }
}
