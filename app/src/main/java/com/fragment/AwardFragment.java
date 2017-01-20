package com.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.example.y3.model.Award;
import com.com.example.y3.model.User;
import com.example.y3.flashclick.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.y3.adapter.AwardAdapter;
import com.y3.adapter.LeaderAdapter;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AwardFragment extends Fragment  {


    private DatabaseReference mDatabase;
    private List<Award> awardList2;
    private String faceboobID;
    private TextView weeklyText, monthlyText;
    private ImageView weekImgView, monthImgView;
    private RecyclerView recyclerView;
    private AwardAdapter adapter2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_award, container, false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        declareVarliables();
        loadData();
    }

    public AwardFragment() {
        // Required empty public constructor
    }

    public void declareVarliables(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        weeklyText = (TextView)getView().findViewById(R.id.weeklyFive);
//        monthlyText = (TextView) getView().findViewById(R.id.monthlyTen);
//        weekImgView = (ImageView) getView().findViewById(R.id.imgWeekly);
//        monthImgView = (ImageView) getView().findViewById(R.id.imgMonthly);

        awardList2 = new ArrayList<Award>();
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        adapter2 = new AwardAdapter(getActivity(), awardList2);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager =  new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter2);

    }

    public void loadData(){

            mDatabase.child("AmazonFiveGiftCardCan").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            Award award = dataSnapshot.getValue(Award.class);

                            awardList2.add(award);
                            // [START_EXCLUDE]
                            if (award == null) {
                                // User is null, error out
                                //  Toast.makeText(MainActivity.this,
                                //     "Error: could not fetch user.",
                                //   Toast.LENGTH_SHORT).show();
                            } else {
                                // get data
//                                Toast.makeText(getActivity(),
//                                        "desc " + award.getImgDescription() + " and url " +award.getImgUrl(),
//                                        Toast.LENGTH_SHORT).show();

                              //  weeklyText.setText(award.getImgDescription());

                              //  GetImage(award.getImgUrl(), weekImgView);

                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("", "getUser:onCancelled", databaseError.toException());
                        }
                    });

        mDatabase.child("AmazonTenGiftCardCan").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Award award = dataSnapshot.getValue(Award.class);

                        awardList2.add(award);
                        // [START_EXCLUDE]
                        if (award == null) {
                            // User is null, error out
                            //  Toast.makeText(MainActivity.this,
                            //     "Error: could not fetch user.",
                            //   Toast.LENGTH_SHORT).show();
                        } else {
                            // get data
//                            Toast.makeText(getActivity(),
//                                    "desc " + award.getImgDescription() + " and url " +award.getImgUrl(),
//                                    Toast.LENGTH_SHORT).show();

                           // monthlyText.setText(award.getImgDescription());
                           // GetImage(award.getImgUrl(), monthImgView);
                        }

                        adapter2.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }


    public void GetImage(String url, ImageView imgView){
       // String url = "https://graph.facebook.com/" + fbId + "/picture?width=200&height=150";
         new ImageLoadTask(url, imgView).execute();
    }
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

}
