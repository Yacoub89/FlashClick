package com.y3.flashclick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.y3.model.Award;

/**
 * Created by Yacoub on 2016-09-01.
 */
public class SplashScreen extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

       // Award award = new Award("https://dl.dropboxusercontent.com/s/8bq1ymegh2wqmz7/AmzGiftCardCan.png?dl=0", "This Month's's Award is a $10 Amazon gift card.");
       //mDatabase.child("AmazonTenGiftCardCan").setValue(award);
//

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    // Initialize Firebase Auth
//                    boolean access = isNetworkAvailable();
//                    if (access){

                    OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

                        if (mFirebaseUser == null) {
                            // Not logged in, launch the Log In activity
                            if (opr.isDone()) {
                                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                                // and the GoogleSignInResult will be available instantly.
//                                Log.d("", "Got cached sign-in");
                                GoogleSignInResult result = opr.get();
                                handleSignInResult(result);
                            } else
                            {
                                // If the user has not previously signed in on this device or the sign-in has expired,
                                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                                // single sign-on will occur in this branch.
                                // showProgressDialog();
                                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                                    @Override
                                    public void onResult(GoogleSignInResult googleSignInResult) {
                                        // hideProgressDialog();
                                        handleSignInResult(googleSignInResult);
                                    }
                                });

                            }
                           // loadLogInView();
                        }
                        else
                        {
                            loadMainActivityView();
                        }
//                    }
//                    else
//                    {
//                        showNoInternetToast();
//                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
       // Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void showNoInternetToast(){
        Toast.makeText(getApplication(), "No intenet access, please connect to the internet.", Toast.LENGTH_SHORT).show();
    }
    private void loadMainActivityView() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void loadLogInView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    public void onStart() {
        super.onStart();
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            loadMainActivityView();
        } else {
            // Signed out, show unauthenticated UI.
            loadLogInView();
        }
    }
    @Override
    public void onClick(View v) {

    }


}
