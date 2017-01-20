package com.example.y3.flashclick;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.com.example.y3.model.User;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fragment.AwardFragment;
import com.fragment.LeaderBoardFragment;
import com.fragment.MainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private String faceboobID, strName, strEmail, proPic;
    boolean access;

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
        points = userDetail.getInt("points", 0);
        faceboobID = userDetail.getString("fbID", "");
        strName = userDetail.getString("name", "");
        strEmail = userDetail.getString("email", "");
        proPic = userDetail.getString("proPic", "");
    }
    public void declareVarliables(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
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
            FirebaseAuth.getInstance().signOut();
            if (LoginManager.getInstance() != null){
                LoginManager.getInstance().logOut();
            }
            loadLogInView();
        } else if (id == R.id.nav_award) {
           // saveUser();
            displayView(2);
        }
        else if (id == R.id.nav_leaderBoard) {
            displayView(3);
        }
//        else if (id == R.id.nav_setting) {
//

        }
        else
        {
            showNoInternetToast();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void loadLogInView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    // loads data from frebase db
    private void loadData(){
        mDatabase.child("users").child(faceboobID).addValueEventListener(
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
                            Toast.makeText(MainActivity.this, "name " + user.getFullName() + " and points " +user.getPoints(), Toast.LENGTH_SHORT).show();
                            userName.setText(user.getFullName());
                            points = user.getPoints();
                            email.setText(user.getEmail());
//                            savePreferences(faceboobID);
                            GetImage(faceboobID);
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
        editor.putString("fbID", faceboobID);
        editor.putString("email", strEmail);
        editor.putString("name",  strName);
        editor.putInt("points",  points);
        editor.apply();
    }
    public void GetImage(String fbId){
        String url = "https://graph.facebook.com/" + fbId + "/picture?width=200&height=150";
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
            User user = new User(strName, strEmail , points, faceboobID);
            mDatabase.child("users").child(faceboobID).setValue(user);
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

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
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

}




