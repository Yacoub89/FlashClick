package com.y3.flashclick;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.y3.fragment.AwardFragment;
import com.y3.fragment.LeaderBoardFragment;
import com.y3.fragment.MainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.y3.model.User;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

   // public static int ADD_ONE_POINT = 1;
    TextView pointText, nameText, userName, email;
    //Button clickButton;
    //TextView timer;
  //  CountDownTimer mCountDownTimer, countDownClickTimer;
    //long mInitialTime;
    int limitCounter;
    private int points;
    private long clickTimer ;
    private DatabaseReference mDatabase;
    private  int numOfClicks;
    private ImageView navImage;
    private String ID, strName, strEmail, photo, source;
    boolean access;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        limitCounter = 0;
        displayView(1);
       declareVarliables();
        loadReferenecesData();


        if(isNetworkAvailable()) {
            loadData();
        }
        else
        {
            showNoInternetToast();

        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void showNoInternetToast(){
        Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
    }
    public void loadReferenecesData(){
        // get the saved
        SharedPreferences sharedPref = this.getSharedPreferences("MyPref", MODE_PRIVATE);
         numOfClicks = sharedPref.getInt("numOfClicks", 0);
        clickTimer = sharedPref.getLong("clickTimer", 0);
        SharedPreferences userDetail = this.getSharedPreferences("userDetail", MODE_PRIVATE);
       // points = userDetail.getInt("points", 0);
        ID = userDetail.getString("ID", "");
        strName = userDetail.getString("name", "");
        strEmail = userDetail.getString("email", "");
        photo = userDetail.getString("photo", "");
        source = userDetail.getString("source", "");
    }
    public void declareVarliables(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        userName = (TextView)hView.findViewById(R.id.userNameNav);
        email = (TextView) hView.findViewById(R.id.emailNav);
        navImage = (ImageView) hView.findViewById(R.id.imageNav);
//        clickButton = (Button) findViewById(R.id.push_button);
        pointText = (TextView) findViewById(R.id.point_text);
       // nameText = (TextView) findViewById(R.id.text_name);
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        timer = (TextView) findViewById(R.id.timer);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(isNetworkAvailable()) {
            loadData();
        if (id == R.id.nav_click) {
            displayView(1);
        } else if (id == R.id.nav_signOut) {
            saveUser();
            signout();
        } else if (id == R.id.nav_award) {
           // saveUser();
            displayView(2);
        }
        else if (id == R.id.nav_leaderBoard) {
            displayView(3);
        }

        }
        else
        {
            showNoInternetToast();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void signout(){

        facebookSignout();
        googleSignout();
        loadLogInView();
    }
    public void facebookSignout(){
        FirebaseAuth.getInstance().signOut();
        if (LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }
    }
    public void googleSignout(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //   updateUI(false);
                    }
                });
    }
    private void loadLogInView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    // loads data from frebase db
    private void loadData(){
        mDatabase.child("users").child(ID).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        // [START_EXCLUDE]
                        if (user == null) {
                        }
                        else {
                            // get data
                          //  Toast.makeText(MainActivity.this, "name " + user.getFullName() + " and points " +user.getPoints(), Toast.LENGTH_SHORT).show();
                            userName.setText(user.getFullName());
                            points = user.getPoints();
                            email.setText(user.getEmail());
//                            savePreferences(faceboobID);
                            photo = user.getPhoto();
                            GetImage(user.getPhoto());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    public void savePreferences(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("userDetail", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ID", ID);
        editor.putString("email", strEmail);
        editor.putString("name",  strName);
       // editor.putInt("points",  points);
        editor.putString("source",  source);
        editor.putString("photo",  photo);
        editor.apply();
    }
    public void GetImage(String url){
        new ImageLoadTask(url, navImage).execute();
    }
    @Override
    protected void onStart() {
        super.onStart();
      //  addCountDownTimer();
        loadReferenecesData();
//        if((clickTimer != 0) || (numOfClicks == 500)){
//            addTenMinTimer();
//        }

    }
    @Override
    protected  void onRestart(){
        super.onRestart();

        if(isNetworkAvailable()){
            loadData();
        }
        else
        showNoInternetToast();;

//        addCountDownTimer();
       loadReferenecesData();
//        if((clickTimer != 0) || (numOfClicks == 500)){
//            addTenMinTimer();
//        }
    }
    @Override
    protected  void onPause(){
        super.onPause();
        saveUser();
        //save num of clicks in file to retrive later
       // saveNumOClicks();
       // saveClickTimer();
    }
    @Override
    protected  void onStop(){
        super.onStop();
        saveUser();
       // saveNumOClicks();
        //saveClickTimer();
    }
    @Override
    protected  void onDestroy(){
        super.onDestroy();
        saveUser();
      //  saveNumOClicks();
        //saveClickTimer();
    }
    public void saveUser(){
        loadReferenecesData();
        if (isNetworkAvailable()){
            User user = new User(strName, strEmail , points, ID, photo, source);
            mDatabase.child("users").child(ID).setValue(user);
        }
        else
        {
            savePreferences();
        }

    }
//    public void saveNumOClicks(){
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putInt("numOfClicks", numOfClicks);
//        editor.apply();
//    }
//    public void saveClickTimer(){
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putLong("clickTimer", clickTimer);
//        editor.apply();
//    }
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
    private void displayView(int position) {

            // update the main content by replacing fragments
            Fragment fragment = null;
            switch (position) {
                case 1:
                    fragment = new MainFragment();
                    break;
                case 2:
                    saveUser();
                    fragment = new AwardFragment();
                    break;
                case 3:
                    saveUser();
                    fragment = new LeaderBoardFragment();
                    break;
//                case 5:
//                    fragment = new BrowseRoomFragment();
//                    break;
                default:
                    break;
            }

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

            }
            // error in creating fragment
            else {
                Log.e("MainActivity", "Error in creating fragment");
            }
    }
    @Override
    public void onClick(View v) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("", "onConnectionFailed:" + connectionResult);
    }

}




