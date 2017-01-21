package com.y3.fragment;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.y3.flashclick.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.y3.adapter.LeaderAdapter;
import com.y3.model.LeaderBoard;
import com.y3.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderBoardFragment extends Fragment {


    private DatabaseReference mDatabase;
    private List<User> userList;
    private ProgressDialog pDialog;
    private ListView listView;
   // private LeaderBoardAdapter adapter;
    private RecyclerView recyclerView;
    private LeaderAdapter adapter2;
    private int i = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    View view;


    public LeaderBoardFragment() {
        // Required empty public cons   tructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_leader_board, container, false);

        getActivity().setTitle("Leaderboard");
        declareVarliables();


        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        declareVarliables();
        loadData();
        initializeAds();
      //  mSwipeRefreshLayout.setRefreshing(true);

        // Setup refresh listener which triggers new data loading
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                declareVarliables();
                loadData();
            }
        });
        // list view listener
        //addListViewListener();
    }

    public void initializeAds(){
//       android_id = Settings.Secure.getString(getContext().getContentResolver(),
//                Settings.Secure.ANDROID_ID);

        MobileAds.initialize(this.getContext(),getString(R.string.banner_ad_unit_id));

        AdView mAdView = (AdView) getView().findViewById(R.id.adView_lead);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("859CC85C99251F3D87697496288BB29F")
                //.addTestDevice("9CEEB49FBA7B7C9AB88FCF7BDCBE686B") //hamood
                .build();
        mAdView.loadAd(adRequest);
    }

    public void declareVarliables() {

        pDialog = new ProgressDialog(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userList = new ArrayList<User>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);


        // Configure the refreshing colors
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        recyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter2 = new LeaderAdapter(getActivity(), userList);
        recyclerView.setAdapter(adapter2);
        // }
        recyclerView.setLayoutManager(MyLayoutManager);

    }


    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    public void addListViewListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;
            }
        });

    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    private void showDialog(){
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
    }
    public void loadData(){
        LeaderBoard leader;
        userList.clear();

        mDatabase.child("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        showDialog();

                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        int length = (int) dataSnapshot.getChildrenCount();
                        //String[] sampleString = new String[length];
                        while(i < length) {
                            User user = iterator.next().getValue(User.class);
                            userList.add(user);
//                                Toast.makeText(getActivity(),
//                                        "name " + user.getFullName() + " and point " +user.getPoints(),
//                                        Toast.LENGTH_SHORT).show();
                            i++;
                        }
                        hidePDialog();
                        sortList(userList);

                        adapter2.notifyDataSetChanged();
                        i = 0;
                        mSwipeRefreshLayout.setRefreshing(false);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }
    public List<User> sortList(List<User> list){
        int temp;

        for(int i = 1; i < list.size(); i++){
            for(int j = i; j > 0; j--){
                if(list.get(j).getPoints() > list.get(j-1).getPoints()){
                    temp = j;
                    Collections.swap(list, j, j - 1);
                    //Collections.swap(list, j - 1, temp);
                }
            }
        }
        return list;
    }
}
