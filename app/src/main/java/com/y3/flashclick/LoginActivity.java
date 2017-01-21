
package com.y3.flashclick;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.y3.model.User;
import com.google.android.gms.auth.api.Auth;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    LoginResult mloginResult;
    JSONObject dataObject;
    String ID, source;
    String userID, PersonName, PersonPhotoUrl,Email;
    Bundle bFacebookData;
    Bundle bundle = new Bundle();
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private SignInButton btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(com.y3.flashclick.R.layout.activity_login);

        boolean access = isNetworkAvailable();
        this.setTitle("Login");

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

        // google +

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());

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
                   addUser("Facebook", ID);

                } else {

                }
            }
        };
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            //Log.e(TAG, "display name: " + acct.getDisplayName());

            PersonName = acct.getDisplayName();
            PersonPhotoUrl = acct.getPhotoUrl().toString();
            Email = acct.getEmail();
             ID = acct.getId();

//            Log.e(TAG, "Name: " + personName + ", email: " + email
//                    + ", Image: " + personPhotoUrl);

           addUser("Google", acct.getId());

            //txtName.setText(personName);
           // txtEmail.setText(email);
//            Glide.with(getApplicationContext()).load(personPhotoUrl)
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imgProfilePic);

        } else {
            // Signed out, show unauthenticated UI.
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

    private void addUser(String source2, String ID2){
        // check if user exist before saving to db
       // SharedPreferences userDetail = getApplication().getSharedPreferences("userDetail", MODE_PRIVATE);
       // ID = userDetail.getString("ID", "");
        if (ID2 == null){
            ID2 = bundle.getString("idFacebook");
        }
        this.ID = ID2;
        this.source = source2;

         mDatabase.child("users").child(ID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            try{
                                writeNewUser(ID , PersonName, Email, PersonPhotoUrl, 0, source);
                            }catch(Exception e){
                                Log.e("addUser()", e.getMessage());
                            }
                        } else {
                            try{
                                toMainActivity(ID, PersonName, Email, PersonPhotoUrl, source);
                            }catch(Exception e){
                                Log.e("addUser()", e.getMessage());
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
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
//        else
//        {
//
//        }
    }
    private Bundle getFacebookData(JSONObject object) {

        dataObject = object;
        try {

            userID = object.getString("id");
            //try {
                //URL profile_pic = new URL("https://graph.facebook.com/" + userID + "/picture?width=200&height=150");

                //String proPic = "https://graph.facebook.com/[userID]/picture?width=200&height=150";

//                SharedPreferences pref = getApplicationContext().getSharedPreferences("userDetail", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putString("ProPic", proPic);
//                editor.putString("ID", userID);
//                editor.apply();


            PersonName = object.getString("name");
            PersonPhotoUrl = "https://graph.facebook.com/" + object.getString("id") + "/picture?width=200&height=150";
            Email = object.getString("email");
            ID = userID;

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
    private void writeNewUser(String id , String name, String email, String photo, int points, String source) {

        User user = new User(name, email, points, id, photo, source);
        mDatabase.child("users").child(id).setValue(user);

        toMainActivity(id, name, email, photo, source);
    }
    public void toMainActivity(String id , String name, String email, String photo, String source){


        SharedPreferences pref = getApplicationContext().getSharedPreferences("userDetail", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ID", id);
        editor.putString("email", email);
        editor.putString("name",  name);
        editor.putString("source",  source);
        editor.putString("photo",  photo);
        editor.apply();


        Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mIntent);
    }
    @Override
    public void onStart() {
        super.onStart();
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else
//        {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//
//        }

    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading..");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
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
