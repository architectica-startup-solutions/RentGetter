package e.sanjay.kangaroorooms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllVehiclesActivity extends AppCompatActivity {

    RecyclerView vehiclesRecyclerView;
    Toolbar toolbar;
    private TextView mToolText;
    List<String> mVehicleNames;
    List<String> mVehicleLocations;
    List<String> mPricePerHour;
    List<String> mPricePerDay;
    List<String> mVehicleImages;
    List<String> VendorNames;
    ArrayList<Integer> mFavouriteImages = new ArrayList<Integer>(100);
    ArrayList<Integer> mBlock = new ArrayList<Integer>(100);
    List<String> vehicleType;
    List<String> orderId,txnAmount,bankTxnId,txnId,bankName;
    List<String> mCity;
    static List<String> vendorUids;
    List<String> noOfVehicles;
    List<String> noOfVendors;
    ProgressDialog pd;
    HotelAdapter mHotelAdapter;
    DatabaseReference vehiclesReference;
    String isAddedToFavourites;
    String district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_vehicles);

        HotelAdapter.content = "AllVehicles";

        mVehicleNames = new ArrayList<String>();
        mVehicleLocations = new ArrayList<String>();
        mPricePerHour = new ArrayList<String>();
        mPricePerDay = new ArrayList<String>();
        mVehicleImages = new ArrayList<String>();
        VendorNames = new ArrayList<String>();
        orderId = new ArrayList<String>();
        txnAmount = new ArrayList<String>();
        bankTxnId = new ArrayList<String>();
        txnId = new ArrayList<String>();
        bankName = new ArrayList<String>();
        mCity = new ArrayList<String>();
        vehicleType = new ArrayList<String>();
        vendorUids = new ArrayList<String>();
        noOfVehicles = new ArrayList<String>();
        noOfVendors = new ArrayList<String>();

        Intent intent = getIntent();

        district = intent.getStringExtra(SearchActivity1.EXTRA_MESSAGE);

        vehiclesRecyclerView = (RecyclerView)findViewById(R.id.vehicles_recycler_view);

        vehiclesReference = FirebaseDatabase.getInstance().getReference(district);

        pd = new ProgressDialog(AllVehiclesActivity.this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        assignValuesFromFirebase(vehiclesReference);

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

                for (DataSnapshot shot : dataSnapshot.getChildren()){

                    for (final DataSnapshot chidSnap : shot.getChildren()) {

                        DataSnapshot snapshot = chidSnap.child("ParkingAddress");

                        for (DataSnapshot snap : snapshot.getChildren()){

                            if ("true".equals(snap.child("isVehicleBlocked").getValue(String.class)) || "BlockedByAdmin".equals(snap.child("isVehicleBlocked").getValue(String.class)) || "Pending".equals(snap.child("status").getValue(String.class))){

                                //dont add the room
                            }
                            else {

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
                                noOfVendors.add(snap.child("Address").getValue(String.class));

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

                        mHotelAdapter = new HotelAdapter(AllVehiclesActivity.this,mVehicleImages,mFavouriteImages,mCity,vehicleType,mVehicleNames,mPricePerHour,mPricePerDay,noOfVendors,noOfVehicles,orderId,txnAmount,bankTxnId,txnId,bankName);
                        vehiclesRecyclerView.setAdapter(mHotelAdapter);
                        vehiclesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        vehiclesRecyclerView.setMotionEventSplittingEnabled(false);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                pd.dismiss();
                Toast.makeText(AllVehiclesActivity.this, "error loading data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}


