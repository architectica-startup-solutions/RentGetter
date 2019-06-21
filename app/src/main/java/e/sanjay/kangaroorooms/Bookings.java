package e.sanjay.kangaroorooms;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class Bookings extends Fragment {

    RelativeLayout firstBookingsLayout,notFirstBookingsLayout;
    DatabaseReference bookingsReferencce;
    List<String> roomImages,roomCity,roomType,roomName,roomPricePerHour,roomPricePerDay,noOfVendors,noOfRooms,orderId,txnAmount,bankTxnId,txnId,bankName;
    List<Integer> roomFavouriteImages;
    HotelAdapter mHotelAdapter;
    RecyclerView mHotelsRecyclerView;

    public static Bookings newInstance() {
        // Required empty public constructor
        Bookings fragment = new Bookings();
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
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

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

        mHotelsRecyclerView = view.findViewById(R.id.bookingsRecyclerView);

        firstBookingsLayout = view.findViewById(R.id.firstBookingLayout);

        notFirstBookingsLayout = view.findViewById(R.id.notFirstBookingLayout);

        bookingsReferencce = FirebaseDatabase.getInstance().getReference("Bookings");

        bookingsReferencce.addValueEventListener(new ValueEventListener() {
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

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if(FirebaseAuth.getInstance().getUid().equals(snapshot.child("UserUid").getValue(String.class))){

                        //booked by this user

                        //fetch all the booked rooms

                        roomName.add(snapshot.child("VehicleName").getValue(String.class));
                        roomCity.add(snapshot.child("City").getValue(String.class));
                        roomFavouriteImages.add(R.drawable.ic_favorite_white_24dp);
                        roomType.add("room");
                        orderId.add(snapshot.child("OrderId").getValue(String.class));
                        txnId.add(snapshot.child("TxnId").getValue(String.class));
                        txnAmount.add(snapshot.child("TxnAmount").getValue(String.class));
                        bankName.add(snapshot.child("BankName").getValue(String.class));
                        bankTxnId.add(snapshot.child("BankTxnId").getValue(String.class));
                        roomImages.add(snapshot.child("VehiclePhoto").getValue(String.class));
                        roomPricePerHour.add(snapshot.child("PricePerHour").getValue(String.class));
                        roomPricePerDay.add(snapshot.child("PricePerDay").getValue(String.class));
                        noOfRooms.add(snapshot.child("NoOfVehiclesAvailable").getValue(String.class));
                        noOfVendors.add(snapshot.child("ParkingAddress").getValue(String.class));


                    }

                }

                //if there are no bookings,then we need to show the layout that user do not have any bookings yet

                if(roomName.size() == 0){

                    firstBookingsLayout.setVisibility(View.VISIBLE);
                    notFirstBookingsLayout.setVisibility(View.GONE);

                }
                else{

                    firstBookingsLayout.setVisibility(View.GONE);
                    notFirstBookingsLayout.setVisibility(View.VISIBLE);

                    mHotelAdapter = new HotelAdapter(getActivity(),roomImages,roomFavouriteImages,roomCity,roomType,roomName,roomPricePerHour,roomPricePerDay,noOfVendors,noOfRooms,orderId,txnAmount,bankTxnId,txnId,bankName);
                    mHotelsRecyclerView.setAdapter(mHotelAdapter);
                    mHotelsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mHotelsRecyclerView.setMotionEventSplittingEnabled(false);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }

}
