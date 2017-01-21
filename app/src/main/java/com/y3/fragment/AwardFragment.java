package com.y3.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.y3.flashclick.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.y3.adapter.AwardAdapter;
import com.y3.model.Award;

import java.util.ArrayList;
import java.util.List;

public class AwardFragment extends Fragment  {


    private DatabaseReference mDatabase;
    private List<Award> awardList;
    private RecyclerView recyclerView;
    private AwardAdapter adapter2;
//    FirebaseStorage storage;
//    StorageReference storageRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_award, container, false);

        getActivity().setTitle("Awards");

        awardList = new ArrayList<Award>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter2 = new AwardAdapter(getActivity(), awardList);
        //if (userList.size() > 0 & recyclerView != null) {

        recyclerView.setAdapter(adapter2);
        // }
        recyclerView.setLayoutManager(MyLayoutManager);
        return view;

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

        // Create a storage reference from our app
       //  storage = FirebaseStorage.getInstance();
         //storageRef = storage.getReferenceFromUrl("gs://flashclick-abe30.appspot.com");
        // Create a reference with an initial file path and name


//        weeklyText = (TextView)getView().findViewById(R.id.weeklyFive);
//        monthlyText = (TextView) getView().findViewById(R.id.monthlyTen);
//        weekImgView = (ImageView) getView().findViewById(R.id.imgWeekly);
//        monthImgView = (ImageView) getView().findViewById(R.id.imgMonthly);



    }

    public void loadData(){



//        final long ONE_MEGABYTE = 1024 * 1024;
//
//        StorageReference pathReference = storageRef.child("Awards/AmzGiftCardCan.png");
//
//        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/island.jpg" is returns, use this as needed
//
//                Bitmap myBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                weekImgView.setImageBitmap(myBitmap);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });


            mDatabase.child("AmazonFiveGiftCardCan").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            Award award = dataSnapshot.getValue(Award.class);

                            awardList.add(award);
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

                        awardList.add(award);
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
}
