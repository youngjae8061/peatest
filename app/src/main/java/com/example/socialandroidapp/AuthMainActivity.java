package com.example.socialandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.HostedUIOptions;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.SignOutOptions;
import com.amazonaws.mobile.client.UserState;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class AuthMainActivity extends AppCompatActivity {

    private static final String TAG = AuthMainActivity.class.getSimpleName();

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);

        context = this;

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // Context
                "IDENTITY_POOL_ID", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Log.d(TAG, "onResult: oncreate(try)- addPlugin");
        } catch (AmplifyException e) {
            e.printStackTrace();
        }
        try {
            Amplify.configure(getApplicationContext());
            Log.d(TAG, "onResult: oncreate(try)- configure");
        } catch (AmplifyException e) {
            e.printStackTrace();
        }

        // Check the current auth session
        Amplify.Auth.fetchAuthSession(
                result -> Log.i("AmplifyQuickstart", "onResult : " + result.toString()),
                error -> Log.e("AmplifyQuickstart", "onResult : " + error.toString())
        );

        CommonAction.checkSession(this, true);
    }

    private void _openFacebookLogin() {
        Log.d(TAG, "onResult(유저상태~): " + AWSMobileClient.getInstance().currentUserState());
//        HostedUIOptions hostedUIOptions = HostedUIOptions.builder()
//                .scopes("openid", "email")
//                .identityProvider("Facebook")
//                .build();
//
//        SignInUIOptions signInUIOptions = SignInUIOptions.builder()
//                .hostedUIOptions(hostedUIOptions)
//                .build();

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                Log.d(TAG, "onResult: getinstance.initialize()");
                Log.i(TAG, result.getUserState().toString());
                switch (result.getUserState()){
                    case SIGNED_IN:
                        Intent i = new Intent(AuthMainActivity.this, SettingActivity.class);
                        Log.d(TAG, "onResult: switch SIGNED_IN");
                        startActivity(i);
                        break;
                    case SIGNED_OUT:
                        Log.d(TAG, "onResult: switch SIGNED_OUT");
                        showSignIn();
                        break;
                    default:
                        Log.d(TAG, "onResult: switch DEFAULT");
                        AWSMobileClient.getInstance().signOut();
                        showSignIn();
                        break;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: ", e);
            }
        });

//        AWSMobileClient.getInstance().showSignIn((Activity) context, signInUIOptions, new Callback<UserStateDetails>() {
//            @Override
//            public void onResult(UserStateDetails details) {
//
////                Log.i(TAG, "onResult(유저상태): " + details.getUserState().toString());
//                Log.d(TAG, "onResult(유저상태): " + details.getUserState());
//                Log.d(TAG, "onResult(유저토큰): " + details.getDetails().get("token"));
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e(TAG, "onError: ", e);
//            }
//        });
    }

    private void _openGoogleLogin() {
        // Add code here
        HostedUIOptions hostedUIOptions = HostedUIOptions.builder()
                .scopes("openid", "email")
                .identityProvider("Google")
                .build();

        SignInUIOptions signInUIOptions = SignInUIOptions.builder()
                .hostedUIOptions(hostedUIOptions)
                .build();

        AWSMobileClient.getInstance().showSignIn((Activity) context, signInUIOptions, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails details) {
                Log.d(TAG, "onResult: _openGoogleLogin()" + details.getUserState());
                Log.d(TAG, "onResult: _openGoogleLogin()" + details.getDetails().get("token"));
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: ", e);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent activityIntent = getIntent();
        if (activityIntent.getData() != null && "socialdemoapp".equals(activityIntent.getData().getScheme()))
        {
            Log.d(TAG, "onResult: onResume()");
            AWSMobileClient.getInstance().handleAuthResponse(activityIntent);
//            if (AWSMobileClient.getInstance().handleAuthResponse(activityIntent))
//                CommonAction.checkSession(this, true);
        }
    }

    public void openLogin(View view) {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    public void openRegistration(View view) {
        Intent intent = new Intent(context, SignUpActivity.class);
        startActivity(intent);
    }


    private void showSignIn() {
//        HostedUIOptions hostedUIOptions = HostedUIOptions.builder()
//                .scopes("openid", "email")
//                .identityProvider("Facebook")
//                .build();

//        SignInUIOptions signInUIOptions = SignInUIOptions.builder()
//                .hostedUIOptions(hostedUIOptions)
//                .build();

        Log.d(TAG, "onResult: showSignIn()");
        try {
            HostedUIOptions hostedUIOptions = HostedUIOptions.builder()
                    .scopes("openid", "email")
                    .identityProvider("Facebook")
                    .build();
            Log.d(TAG, "onResult: try{} in showSignIn()");
            AWSMobileClient.getInstance().showSignIn(this,
                    SignInUIOptions.builder()
                            .hostedUIOptions(hostedUIOptions)
                            .build());
 //                   SignInUIOptions.builder().nextActivity(SettingActivity.class).build());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void openFacebookLogin(View view) {
        _openFacebookLogin();
    }

    public void openGoogleLogin(View view) {
        _openGoogleLogin();
    }

}
