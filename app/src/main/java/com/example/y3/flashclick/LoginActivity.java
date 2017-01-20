
package com.example.y3.flashclick;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.com.example.y3.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    LoginResult mloginResult;
    JSONObject dataObject;
    String fbID;
    String userID;
    Bundle bFacebookData;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);


        boolean access = isNetworkAvailable();

        addListener();
        initilizeVaribles();
        fbLogInButton();

        if(!access) {

            showNoInternet();
        }
    }

    public void showNoInternet(){
        info.setText("No internet connection.");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void initilizeVaribles(){
        bFacebookData = new Bundle();
        loginButton = (LoginButton)findViewById(R.id.login_button);
        info = (TextView)findViewById(R.id.info);
        // facebook login
        callbackManager = CallbackManager.Factory.create();
        //database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
    public void addListener(){
        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mFirebaseUser = user;
                if (mFirebaseUser != null) {
                    // User is signed in
                   addUser();

                } else {

                }
            }
        };
    }
    public void fbLogInButton(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());

                mloginResult = loginResult;
                info.setText("Login successfully");

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                bFacebookData = getFacebookData(object);
                            }
                        });
                Bundle parameters = bFacebookData;
             //   fbID = parameters.getString("idFacebook");
                parameters.putString("fields", "id, name, email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       // Log.d("", "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                           // Log.w("", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUser(){
        // check if user exist before saving to db
        SharedPreferences userDetail = getApplication().getSharedPreferences("userDetail", MODE_PRIVATE);
        fbID = userDetail.getString("fbID", "");

        mDatabase.child("users").child(fbID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                             //User is null, error out
                             //Log.e("", "User " + userID + " is unexpectedly null");
                            Toast.makeText(LoginActivity.this, "New User", Toast.LENGTH_SHORT).show();

                            // Write user to dt

                            try{


                                writeNewUser(fbID , bundle.getString("name"), bundle.getString("email"), 0, fbID);
                            }catch(Exception e){

                            }
                        } else {

                            try{
                                toMainActivity(fbID, bundle.getString("name"),  bundle.getString("email"));
                            }catch(Exception e){

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private Bundle getFacebookData(JSONObject object) {

        dataObject = object;
        try {

            userID = object.getString("id");
            //try {
                //URL profile_pic = new URL("https://graph.facebook.com/" + userID + "/picture?width=200&height=150");

                String proPic = "https://graph.facebook.com/[userID]/picture?width=200&height=150";

                SharedPreferences pref = getApplicationContext().getSharedPreferences("userDetail", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ProPic", proPic);
                editor.putString("fbID", userID);
                editor.apply();

            bundle.putString("idFacebook", userID);
            if (object.has("name"))
                bundle.putString("name", object.getString("name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));

            return bundle;
        } catch (Exception e) {

        }
        return bundle;

    }
    private void writeNewUser(String id , String name, String email, int points, String facebookID) {

        User user = new User(name, email, points, facebookID);
        mDatabase.child("users").child(id).setValue(user);

        toMainActivity(id, name, email);
    }
    public void toMainActivity(String id , String name, String email){


        SharedPreferences pref = getApplicationContext().getSharedPreferences("userDetail", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("fbID", id);
        editor.putString("email", email);
        editor.putString("name",  name);
        editor.apply();


        Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
//        Bundle mBundle = new Bundle();
//        mBundle.putString("fbID", id);
//        mBundle.putString("email", email);
//        mBundle.putString("name", name);
//        mIntent.putExtras(mBundle);

        startActivity(mIntent);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onRestart(){
        super.onRestart();
        boolean access = isNetworkAvailable();
        if(!access){
            showNoInternet();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
