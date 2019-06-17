package e.sanjay.kangaroorooms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HotelsActivity extends AppCompatActivity {

    int redColorOn;
    private TextView mToolText;
    List<String> mCity;
    private Toolbar toolbar;
    RecyclerView mHotelsRecyclerView;
    HotelAdapter mHotelAdapter;
    private ImageButton mFavouriteIcon;
    private TextView mRoomOriginalCost;
    ProgressDialog pd;
    List<String> mVehicleNames;
    List<String> mVehicleLocations;
    List<String> mPricePerHour;
    List<String> mPricePerDay;
    List<String> mVehicleImages;
    List<String> VendorNames;
    List<String> noOfVehicles;
    List<String> noOfVendors;
    List<Integer> noOfParkingAddresses;
    ArrayList<Integer> mFavouriteImages = new ArrayList<Integer>(100);
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference,mReference;
    List<String> vehicleType;
    List<ParkingAddress> parkingAddresses;
    List<List<ParkingAddress>> itemAddresses;
    String isAddedToFavourites;
    String image;
    String data;
    String district;
    String vehType;
    List<String> orderId,txnAmount,bankTxnId,txnId,bankName;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
            //overridePendingTransition(R.anim.activit_back_in, R.anim.activity_back_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);

        HotelAdapter.content = "ViewVehicles";

        redColorOn = 0;
        mToolText = (TextView) findViewById(R.id.tool_text);
        Intent intent = getIntent();
        district = intent.getStringExtra(SearchActivity1.EXTRA_MESSAGE);
        vehType = intent.getStringExtra(SearchActivity1.EXTRA_VEHICLE);
        mVehicleNames = new ArrayList<String>();
        mVehicleLocations = new ArrayList<String>();
        mPricePerHour = new ArrayList<String>();
        mPricePerDay = new ArrayList<String>();
        mVehicleImages = new ArrayList<String>();
        VendorNames = new ArrayList<String>();
        noOfVehicles = new ArrayList<String>();
        orderId = new ArrayList<String>();
        txnAmount = new ArrayList<String>();
        bankTxnId = new ArrayList<String>();
        txnId = new ArrayList<String>();
        bankName = new ArrayList<String>();
        mCity = new ArrayList<String>();
        vehicleType = new ArrayList<String>();
        parkingAddresses = new ArrayList<ParkingAddress>();
        noOfParkingAddresses = new ArrayList<Integer>();
        itemAddresses = new ArrayList<List<ParkingAddress>>();
        noOfVendors = new ArrayList<String>();
        isAddedToFavourites = "false";

        mToolText.setText(district);
        toolbar = (Toolbar) findViewById(R.id.activity_hotels_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();

        mHotelsRecyclerView = (RecyclerView) findViewById(R.id.hotels_recycler_view);

        pd = new ProgressDialog(HotelsActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(district)){

                    mDatabaseReference = mFirebaseDatabase.getReference(district + "/room");

                    if (mDatabaseReference != null){

                        assignValuesFromFirebase(mDatabaseReference);

                    }

                }
                else{

                    pd.dismiss();
                    Toast.makeText(HotelsActivity.this, "There are no rooms available in this location.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(HotelsActivity.this, "error!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void assignValuesFromFirebase(DatabaseReference databaseReference){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mPricePerDay.clear();
                mPricePerHour.clear();
                mVehicleNames.clear();
                mVehicleImages.clear();
                noOfVehicles.clear();
                mCity.clear();
                vehicleType.clear();
                noOfVendors.clear();
                orderId.clear();
                bankTxnId.clear();
                bankName.clear();
                txnAmount.clear();
                txnId.clear();

                for (final DataSnapshot chidSnap : dataSnapshot.getChildren()) {

                    DataSnapshot snapshot1 = chidSnap.child("ParkingAddress");

                    for (DataSnapshot snap1 : snapshot1.getChildren()){

                        if ("true".equals(snap1.child("isVehicleBlocked").getValue(String.class)) || "BlockedByAdmin".equals(snap1.child("isVehicleBlocked").getValue(String.class)) || "Pending".equals(snap1.child("status").getValue(String.class))){

                            //dont add the room
                        }
                        else {

                            if (snap1.hasChild(FirstActivity.Vehicle)){

                                mVehicleImages.add(chidSnap.child("VehiclePhoto").getValue(String.class));
                                mVehicleNames.add(chidSnap.getKey());
                                mPricePerHour.add(chidSnap.child("PricePerHour").getValue(String.class));
                                mPricePerDay.add(chidSnap.child("PricePerDay").getValue(String.class));
                                noOfVehicles.add(chidSnap.child("NoOfVehiclesAvailable").getValue(String.class));
                                mCity.add(district);
                                vehicleType.add("room");
                                orderId.add(null);
                                bankTxnId.add(null);
                                bankName.add(null);
                                txnAmount.add(null);
                                txnId.add(null);
                                noOfVendors.add(snap1.child("Address").getValue(String.class));

                            }

                        }

                    }

                }

                DatabaseReference favouritesReference = FirebaseDatabase.getInstance().getReference("Users/" + LoginActivity.userUid);

                favouritesReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mFavouriteImages.clear();

                        for (int i=0;i<mVehicleNames.size();i++) {

                            if (dataSnapshot.hasChild("Favourites")) {

                                DataSnapshot snapshot1 = dataSnapshot.child("Favourites");

                                for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {

                                    if (mVehicleNames.get(i).equals(dataSnapshot1.child("VehicleName").getValue(String.class)) && mCity.get(i).equals(dataSnapshot1.child("City").getValue(String.class))) {

                                        if (!dataSnapshot1.hasChild("OrderId")) {
                                            isAddedToFavourites = "true";
                                            break;
                                        }

                                    } else {

                                        isAddedToFavourites = "false";

                                    }

                                }

                                if (isAddedToFavourites.equals("true")) {

                                    mFavouriteImages.add(R.drawable.ic_favorite_red_24dp);
                                } else {

                                    mFavouriteImages.add(R.drawable.ic_favorite_white_24dp);
                                }


                            } else {

                                mFavouriteImages.add(R.drawable.ic_favorite_white_24dp);
                            }
                        }

                        for (int i=0;i<mVehicleNames.size();i++){

                            if (Integer.parseInt(noOfVehicles.get(i)) <= 0){

                                mVehicleImages.remove(i);
                                mVehicleNames.remove(i);
                                mPricePerHour.remove(i);
                                mPricePerDay.remove(i);
                                noOfVehicles.remove(i);
                                mCity.remove(i);
                                vehicleType.remove(i);
                                orderId.remove(i);
                                bankTxnId.remove(i);
                                bankName.remove(i);
                                txnAmount.remove(i);
                                txnId.remove(i);
                                noOfVendors.remove(i);
                                mFavouriteImages.remove(i);

                            }

                        }

                        pd.dismiss();

                        mHotelAdapter = new HotelAdapter(HotelsActivity.this,mVehicleImages,mFavouriteImages,mCity,vehicleType,mVehicleNames,mPricePerHour,mPricePerDay,noOfVendors,noOfVehicles,orderId,txnAmount,bankTxnId,txnId,bankName);
                        mHotelsRecyclerView.setAdapter(mHotelAdapter);
                        mHotelsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        mHotelsRecyclerView.setMotionEventSplittingEnabled(false);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                pd.dismiss();
                Toast.makeText(HotelsActivity.this, "error loading data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

