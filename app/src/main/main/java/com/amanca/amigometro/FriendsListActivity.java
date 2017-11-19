package com.amanca.amigometro;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.amanca.amigometro.adapter.FriendsAdapter;
import com.sweetzpot.stravazpot.activity.api.ActivityAPI;
import com.sweetzpot.stravazpot.activity.model.Activity;
import com.sweetzpot.stravazpot.athlete.api.AthleteAPI;
import com.sweetzpot.stravazpot.athlete.api.FriendAPI;
import com.sweetzpot.stravazpot.authenticaton.api.AuthenticationAPI;
import com.sweetzpot.stravazpot.authenticaton.model.AppCredentials;
import com.sweetzpot.stravazpot.authenticaton.model.LoginResult;
import com.sweetzpot.stravazpot.authenticaton.model.Token;
import com.sweetzpot.stravazpot.common.api.AuthenticationConfig;
import com.sweetzpot.stravazpot.common.api.StravaConfig;
import com.sweetzpot.stravazpot.common.model.Time;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.amanca.amigometro.AmigometroStravaLoginActivity.AMIGOMETRO_CLIENT_ID;
import static com.amanca.amigometro.AmigometroStravaLoginActivity.AMIGOMETRO_CLIENT_SECRET;
import static com.amanca.amigometro.AmigometroStravaLoginActivity.STRAVA_AUTH_TOKEN;

public class FriendsListActivity extends AppCompatActivity {

    private RecyclerView mFriendsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Token mToken;
    private StravaConfig mStravaConfig;
    private ActivityAPI mActivityApi;
    private HashMap<Long,Integer> mFriendsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String strCode =  getIntent().getStringExtra(STRAVA_AUTH_TOKEN);
        getToken(strCode);

        mFriendsRecyclerView = (RecyclerView) findViewById(R.id.friends_recycler_view);

        //TODO check if this is needed
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the Recyc,lerView
        //mFriendsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mFriendsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FriendsAdapter();
        mFriendsRecyclerView.setAdapter(mAdapter);

        //init friends map
        mFriendsMap = new HashMap<Long,Integer>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getToken(final String code)
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AuthenticationConfig config = AuthenticationConfig.create()
                        .debug()
                        .build();
                AuthenticationAPI api = new AuthenticationAPI(config);
                LoginResult result = api.getTokenForApp(AppCredentials.with(AMIGOMETRO_CLIENT_ID, AMIGOMETRO_CLIENT_SECRET))
                        .withCode(code)
                        .execute();

                if (result != null)
                {
                    Log.e("FriendsList", "token =  " + result.getToken().toString());
                    mToken = result.getToken();
                    //get Strava Config
                    mStravaConfig = StravaConfig.withToken(mToken).debug().build();
                   //Build list
                    getFriends();
                }
            }
        });
    }


    /**
     * Retrieve friends from Strava.
     * Will consider a list of activities of the past month.
     */
    //TODO run this in async task
    private void getFriends()
    {

        mActivityApi =  new ActivityAPI(mStravaConfig);

        //Get activities from ne week ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);

        List<Activity> activities = mActivityApi.listMyActivities()
                .execute();

        for(Activity a : activities)
        {
            Log.e("FRIENDS LIST" , "******** activity: " + activities.get(0).getName() + " athletes = " + activities.get(0).getAthleteCount());

            if(a.getAthleteCount() > 1)
            {
                List<Activity> related = mActivityApi.listRelatedActivities(a.getID()).execute();
                for (Activity r : related)
                {
                    Log.e("FRIENDS LIST" , "Inc " + r.getAthlete().getFirstName());
                    if(mFriendsMap.containsKey(r.getAthlete().getID())) {
                        Integer count = mFriendsMap.get(r.getAthlete().getID());
                        mFriendsMap.put(r.getAthlete().getID(), count+1);
                    }
                    else
                    {
                        mFriendsMap.put(r.getAthlete().getID(), 1);
                    }
                }
            }
        }

        Log.e("FRIENDS LIST" , "******** COUNT FINISHED " + mFriendsMap.toString());

    }

}
