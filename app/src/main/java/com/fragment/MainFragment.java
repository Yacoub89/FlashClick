package com.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.example.y3.model.Date;
import com.com.example.y3.model.User;
import com.example.y3.flashclick.MainActivity;
import com.example.y3.flashclick.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {

    public static int ADD_ONE_POINT = 1;
    TextView pointText;// nameText, userName, email;
    Button clickButton;
    TextView timer;
    CountDownTimer mCountDownTimer, countDownClickTimer;
    long mInitialTime;
  //  int limitCounter;
    private int points;
    private long clickTimer ;
    private DatabaseReference mDatabase;
    private  int numOfClicks;
   // private ImageView navImage;
    private String faceboobID, strName, strEmail;
    InterstitialAd mInterstitialAd;
    AdRequest adInterstitialRequest;
  //  private String android_id;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


        declareVarliables();
        loadReferenecesData();
        addListeners();

        if(isNetworkAvailable()) {
            initializeAds();
            loadData();
            clickButton.setEnabled(true);
        }
        else
        {
            showNoInternetToast();
            clickButton.setEnabled(false);

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void showNoInternetToast(){
        Toast.makeText(getActivity(), "No internet connection.", Toast.LENGTH_SHORT).show();
    }

    public void initializeAds(){
//       android_id = Settings.Secure.getString(getContext().getContentResolver(),
//                Settings.Secure.ANDROID_ID);

        MobileAds.initialize(this.getContext(),getString(R.string.banner_ad_unit_id));

        AdView mAdView = (AdView) getView().findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("859CC85C99251F3D87697496288BB29F")
                //.addTestDevice("9CEEB49FBA7B7C9AB88FCF7BDCBE686B") //hamood
                .build();
        mAdView.loadAd(adRequest);

        //Interstitial
        mInterstitialAd = new InterstitialAd(getContext());

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

       adInterstitialRequest = new AdRequest.Builder()
                .addTestDevice("859CC85C99251F3D87697496288BB29F")
               //.addTestDevice("9CEEB49FBA7B7C9AB88FCF7BDCBE686B")// hamood
                .build();
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public void declareVarliables(){
        clickButton = (Button) getView().findViewById(R.id.push_button);
        pointText = (TextView) getView().findViewById(R.id.point_text);
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }
    public void loadReferenecesData(){
        // get the saved info
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        numOfClicks = sharedPref.getInt("numOfClicks", 0);
        clickTimer = sharedPref.getLong("clickTimer", 0);
        numOfClicks = sharedPref.getInt("numOfClicks", 0);
        //clickButton = (Button) getView().findViewById(R.id.push_button);
        SharedPreferences userDetail = getActivity().getSharedPreferences("userDetail", MODE_PRIVATE);
        points = userDetail.getInt("points", 0);
        faceboobID = userDetail.getString("fbID", "");
        strName = userDetail.getString("name", "");
        strEmail = userDetail.getString("email", "");
    }
    public void addListeners(){
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(numOfClicks == 100 || numOfClicks > 100){
                    numOfClicks = 0;
                     controlButton(false);
                    // Load ads into Interstitial Ads
                    mInterstitialAd.loadAd(adInterstitialRequest);
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            Toast.makeText(getContext(), "Ad is loaded!", Toast.LENGTH_SHORT).show();
                            showInterstitial();
                        }

                        @Override
                        public void onAdClosed() {
                            Toast.makeText(getContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
                            controlButton(true);
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            Toast.makeText(getContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLeftApplication() {
                            Toast.makeText(getContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdOpened() {
                            Toast.makeText(getContext(), "Ad is opened!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else
                {
                    addPoint();
                    setPointText(String.valueOf(getPoints()));
                   // controlButton(true);
                    numOfClicks +=1;
                }

//                if(numOfClicks == 500){
//                    setPointText(String.valueOf(getPoints()));
//                    //clickButton.setEnabled(false);
//                    controlButton(false);
//                    clickTimer = DateUtils.DAY_IN_MILLIS * 0 +
//                            DateUtils.HOUR_IN_MILLIS * 0 +
//                            DateUtils.MINUTE_IN_MILLIS * 10 +
//                            DateUtils.SECOND_IN_MILLIS * 0;
//                    saveClickTimer();
//                    numOfClicks = 0;
//                    //show dialog for add.
//                 showAlert();
//                }
//                else
//                {
//                    addPoint();
//                    setPointText(String.valueOf(getPoints()));
//                    controlButton(true);
//                    numOfClicks +=1;
//                }
            }
        });
    }
    private void loadData(){
        mDatabase.child("users").child(faceboobID).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        // [START_EXCLUDE]
                        if (user == null) {
                        }
                        else {
                            points = user.getPoints();
                            pointText.setText(Integer.toString(points));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    public void addPoint(){

        points= Integer.parseInt(pointText.getText().toString()) + ADD_ONE_POINT;
        SavePoints(points);
        saveNumOClicks();
    }
    public void setPointText(String text){
        pointText.setText(text);
    }
    public int getPoints(){
        return points;
    }
    public void controlButton(boolean lock){
        clickButton.setEnabled(lock);
    }
    public void SavePoints(int points){
        SharedPreferences pref = getContext().getSharedPreferences("userDetail", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("points",points);
        editor.apply();
    }
    private void addTenMinTimer(){
    // set 10 min timer
    countDownClickTimer = new CountDownTimer (clickTimer, 1000) {
        StringBuilder time = new StringBuilder();
        @Override
        public void onFinish() {
            clickButton.setText(DateUtils.formatElapsedTime(0));
            clickButton.setText("Click!");
            clickButton.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time.setLength(0);
            // Use days if appropriate
            if(millisUntilFinished > DateUtils.DAY_IN_MILLIS) {
                long count = millisUntilFinished / DateUtils.DAY_IN_MILLIS;
                if(count > 1)
                    time.append(count).append(" days ");
                else
                    time.append(count).append(" day ");

                millisUntilFinished %= DateUtils.DAY_IN_MILLIS;
            }

            time.append(DateUtils.formatElapsedTime(Math.round(millisUntilFinished / 1000d)));
            clickButton.setText(time.toString());
        }
    }.start();

}
    private void addCountDownTimer(){

        timer.setText("");

        mDatabase.child("date").addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Date dateEnd = dataSnapshot.getValue(Date.class);

                        if (dateEnd == null) {
                            // dateSet is null, error out
                            Toast.makeText(getActivity(),
                                    "Error: could not fetch dateSet.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // get data
//                            Toast.makeText(MainActivity.this,
//                                    "day " + dateEnd.getDay() + " and hour " + dateEnd.getHour(),
//                                    Toast.LENGTH_SHORT).show();

                            java.util.Date date = new java.util.Date(Integer.parseInt(dateEnd.getYear()), Integer.parseInt(dateEnd.getMonth()), Integer.parseInt(dateEnd.getDay()),
                                    Integer.parseInt(dateEnd.getHour()), Integer.parseInt(dateEnd.getMinuets()), Integer.parseInt(dateEnd.getSeconds()));

                            Calendar c = Calendar.getInstance();

                            java.util.Date date2 = new java.util.Date( c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.HOUR),
                                    c.get(Calendar.MINUTE),c.get(Calendar.SECOND));

                            int day = date.getDay() - date2.getDay();
                            int minute= date.getMinutes() - date.getMinutes();
                            int hour = date.getHours() - date2.getHours();
                            int seconds = date.getSeconds() - date2.getSeconds();


                            mInitialTime = DateUtils.DAY_IN_MILLIS * ( day) +
                                    DateUtils.HOUR_IN_MILLIS * ( hour) +
                                    DateUtils.MINUTE_IN_MILLIS * ( minute) +
                                    DateUtils.SECOND_IN_MILLIS * ( seconds);

                            mCountDownTimer = new CountDownTimer (mInitialTime, 1000) {
                                StringBuilder time = new StringBuilder();

                                @Override
                                public void onFinish() {
                                    timer.setText(DateUtils.formatElapsedTime(0));
                                    timer.setText("Times Up!");
                                    clickButton.setEnabled(false);
                                }

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    time.setLength(0);
                                    // Use days if appropriate
                                    if(millisUntilFinished > DateUtils.DAY_IN_MILLIS) {
                                        long count = millisUntilFinished / DateUtils.DAY_IN_MILLIS;
                                        if(count > 1)
                                            time.append(count).append(" days ");
                                        else
                                            time.append(count).append(" day ");

                                        millisUntilFinished %= DateUtils.DAY_IN_MILLIS;
                                    }

                                    time.append(DateUtils.formatElapsedTime(Math.round(millisUntilFinished / 1000d)));

                                    timer.setText(time.toString());
                                }
                            }.start();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    public void saveNumOClicks(){
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("numOfClicks", numOfClicks);
        editor.apply();
    }
    public void saveClickTimer(){
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("clickTimer", clickTimer);
        editor.apply();
    }

}
