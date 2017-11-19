package com.amanca.amigometro;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by amanda on 19/11/17.
 */

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<AmigometroStravaLoginActivity> rule  = new  ActivityTestRule<AmigometroStravaLoginActivity>(AmigometroStravaLoginActivity.class)
    {
        @Override
        protected Intent getActivityIntent() {
            InstrumentationRegistry.getTargetContext();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            //intent.putExtra("MYKEY", "Hello");
            return intent;
        }
    };


    @Test
    public void checkAppContext(){
        Context ctx = InstrumentationRegistry.getTargetContext();
        assertEquals("com.amanca.amigometro", ctx.getPackageName());
    }


   @Test
    public void checkListIsPresent()
    {
        AmigometroStravaLoginActivity fl = rule.getActivity();
        View list =  fl.findViewById(R.id.login_button);
        assertNotNull(list);
        assertTrue(list instanceof RecyclerView);

    }

}
