package com.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.com.example.y3.model.Award;
import com.com.example.y3.model.LeaderBoard;
import com.com.example.y3.model.User;
import com.example.y3.flashclick.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.y3.adapter.LeaderAdapter;
import com.y3.adapter.LeaderBoardAdapter;

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
    private LeaderBoardAdapter adapter;
    private RecyclerView recyclerView;
    private LeaderAdapter adapter2;
    private int i = 0;
    public LeaderBoardFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leader_board, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        declareVarliables();
        loadData();
        // list view listener
        //addListViewListener();
    }
    public void declareVarliables() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userList = new ArrayList<User>();
        //listView = (ListView) getView().findViewById(R.id.list);
        pDialog = new ProgressDialog(getContext());
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        adapter2 = new LeaderAdapter(getActivity(), userList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager =  new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter2);
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

                       // adapter = new LeaderBoardAdapter(getActivity(), userList);
                       // listView.setAdapter(adapter);

                        adapter2.notifyDataSetChanged();

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
