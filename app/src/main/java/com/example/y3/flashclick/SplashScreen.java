package com.example.y3.flashclick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.com.example.y3.model.Award;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Yacoub on 2016-09-01.
 */
public class SplashScreen extends Activity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

//        Award award = new Award("https://dl.dropboxusercontent.com/s/8bq1ymegh2wqmz7/AmzGiftCardCan.png?dl=0", "This Month's Award is a $10 Amazon gift card.");
//       mDatabase.child("AmazonTenGiftCardCan").setValue(award);
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
                        if (mFirebaseUser == null) {
                            // Not logged in, launch the Log In activity
                            loadLogInView();
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

//        SharedPreferences pref = getApplicationContext().getSharedPreferences("userDetail", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("ID", mFirebaseUser.getUid());
//        editor.putString("email", mFirebaseUser.getEmail());
//        editor.putString("token",  mFirebaseUser.getToken(true).toString());
//        editor.putString("name",  mFirebaseUser.getDisplayName());
//        editor.apply();

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


}
