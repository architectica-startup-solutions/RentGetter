package e.sanjay.kangaroorooms;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedHotelFragment extends Fragment {

    DatabaseReference userReference;
    RelativeLayout notFirstFavouritesLayout,firstFavouritesLayout;
    List<String> roomImages,roomCity,roomType,roomName,roomPricePerHour,roomPricePerDay,noOfVendors,noOfRooms,orderId,txnAmount,bankTxnId,txnId,bankName;
    List<Integer> roomFavouriteImages;
    HotelAdapter mHotelAdapter;
    RecyclerView mHotelsRecyclerView;

    public static SavedHotelFragment newInstance() {
        // Required empty public constructor
        SavedHotelFragment fragment = new SavedHotelFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_hotel, container, false);

        roomImages = new ArrayList<String>();
        roomCity = new ArrayList<String>();
        roomFavouriteImages = new ArrayList<Integer>();
        roomType = new ArrayList<String>();
        roomName = new ArrayList<String>();
        roomPricePerHour = new ArrayList<String>();
        roomPricePerDay = new ArrayList<String>();
        noOfVendors = new ArrayList<String>();
        noOfRooms = new ArrayList<String>();
        orderId = new ArrayList<String>();
        txnAmount = new ArrayList<String>();
        bankName = new ArrayList<String>();
        bankTxnId = new ArrayList<String>();
        txnId = new ArrayList<String>();

        userReference = FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getUid());

        firstFavouritesLayout = view.findViewById(R.id.firstFavouritesLayout);

        notFirstFavouritesLayout = view.findViewById(R.id.notFirstFavouritesLayout);

        mHotelsRecyclerView = view.findViewById(R.id.favouritesRecyclerView);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                roomImages.clear();
                roomCity.clear();
                roomFavouriteImages.clear();
                roomType.clear();
                roomName.clear();
                roomPricePerHour.clear();
                roomPricePerDay.clear();
                noOfVendors.clear();
                noOfRooms.clear();
                orderId.clear();
                txnAmount.clear();
                bankName.clear();
                bankTxnId.clear();
                txnId.clear();

                if(dataSnapshot.hasChild("Favourites")){

                    firstFavouritesLayout.setVisibility(View.GONE);
                    notFirstFavouritesLayout.setVisibility(View.VISIBLE);

                    //show all the favourite rooms of the user

                    DataSnapshot dataSnapshot1 = dataSnapshot.child("Favourites");

                    for(DataSnapshot snapshot : dataSnapshot1.getChildren()){

                        String name = snapshot.child("VehicleName").getValue(String.class);
                        String city = snapshot.child("City").getValue(String.class);

                        roomName.add(name);
                        roomCity.add(city);
                        roomFavouriteImages.add(R.drawable.ic_favorite_red_24dp);
                        roomType.add("room");
                        orderId.add(null);
                        txnId.add(null);
                        txnAmount.add(null);
                        bankName.add(null);
                        bankTxnId.add(null);

                        DatabaseReference roomReference = FirebaseDatabase.getInstance().getReference(city + "/room/" + name);

                        roomReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                roomImages.add(dataSnapshot.child("VehiclePhoto").getValue(String.class));
                                roomPricePerHour.add(dataSnapshot.child("PricePerHour").getValue(String.class));
                                roomPricePerDay.add(dataSnapshot.child("PricePerDay").getValue(String.class));
                                noOfRooms.add(dataSnapshot.child("NoOfVehiclesAvailable").getValue(String.class));

                                DataSnapshot addressSnapshot = dataSnapshot.child("ParkingAddress");

                                noOfVendors.add(Long.toString(addressSnapshot.getChildrenCount()));

                                mHotelAdapter = new HotelAdapter(getActivity(),roomImages,roomFavouriteImages,roomCity,roomType,roomName,roomPricePerHour,roomPricePerDay,noOfVendors,noOfRooms,orderId,txnAmount,bankTxnId,txnId,bankName);
                                mHotelsRecyclerView.setAdapter(mHotelAdapter);
                                mHotelsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                mHotelsRecyclerView.setMotionEventSplittingEnabled(false);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }
                else{

                    firstFavouritesLayout.setVisibility(View.VISIBLE);
                    notFirstFavouritesLayout.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}


