package com.amanca.amigometro;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amanca.amigometro.R;
import com.sweetzpot.stravazpot.authenticaton.api.AccessScope;
import com.sweetzpot.stravazpot.authenticaton.api.AuthenticationAPI;
import com.sweetzpot.stravazpot.authenticaton.api.StravaLogin;
import com.sweetzpot.stravazpot.authenticaton.model.AppCredentials;
import com.sweetzpot.stravazpot.authenticaton.model.LoginResult;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginActivity;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginButton;
import com.sweetzpot.stravazpot.common.api.AuthenticationConfig;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.net.URL;

import static com.sweetzpot.stravazpot.authenticaton.api.ApprovalPrompt.AUTO;


public class AmigometroStravaLoginActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final int RQ_LOGIN = 1001;
    static final int AMIGOMETRO_CLIENT_ID = 16527;
    static final String AMIGOMETRO_CLIENT_SECRET = "a0320e1c5abb89721c13e5a793ae73d5330bf837";
    static final String AMIGOMETRO_CALLBACK_URL = "http://sample-env.j3h2mxghvt.us-west-2.elasticbeanstalk.com";
    public static final String STRAVA_AUTH_TOKEN = "token";
    private TextView mCodeTv;
    StravaLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigometro_strava_login);
        mCodeTv = (TextView) findViewById(R.id.textView);
        loginButton = (StravaLoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        Intent intent = StravaLogin.withContext(this)
                .withClientID(AMIGOMETRO_CLIENT_ID)
                .withRedirectURI(AMIGOMETRO_CALLBACK_URL)
                .withApprovalPrompt(AUTO)
                .withAccessScope(AccessScope.VIEW_PRIVATE_WRITE)
                .makeIntent();
        startActivityForResult(intent, RQ_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RQ_LOGIN && resultCode == RESULT_OK && data != null) {
            String codeStr = data.getStringExtra(StravaLoginActivity.RESULT_CODE);
            Log.d("Strava code", codeStr);
            mCodeTv.setText(codeStr);
            Intent i = new Intent(AmigometroStravaLoginActivity.this, FriendsListActivity.class);
            i.putExtra(STRAVA_AUTH_TOKEN, codeStr);
            startActivity(i);
        }
    }



}

