package e.sanjay.kangaroorooms;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HotelActivity_1 extends AppCompatActivity{

    Toolbar toolbar;
    private ImageView mVehicleImageView;
    private TextView mVehicleName;
    private TextView mVehicleLocation;
    private TextView mVehicleCity;
    static TextView mnetPayableAmount;
    TextView bookingNoOfVehiclesTextView,pickUpTime,pickUpDateTextView,deliveryDateTextView,deliveryTime;
    //ProgressDialog pd;

    int mHotelImage;
    RecyclerView OYORecyclerView;
    HotelAdapter mOYOAdapter;
    List<String> mCity;
    List<String> vehicleType;
    private TextView mPersonName;
    private TextView mPleaseReadTextView;

    ArrayList<Integer> mFavouriteImages = new ArrayList<Integer>(100);
    // List<String> mVehicleNo;
    List<String> mVehicleNames;
    List<String> mVehicleLocations;
    List<String> mPricePerHour;
    List<String> mPricePerDay;
    List<String> mVehicleImages;
    List<String> VendorNames;
    List<String> noOfVehicles,noOfVendors;
    List<ParkingAddress> parkingAddresses;
    List<String> orderId,txnAmount,bankTxnId,txnId,bankName;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference,mReference,parkingAddressReference;
    Intent intent;
    String isAddedToFavourites;
    String district;
    String vehType;
    static String selectedAddress,selectedVendor,noOfVehiclesWithVendor;
    LinearLayout bookingDetailsLayout,calendarLayout,nonBookingLayout;
    LinearLayout bookVehicleLayout;
    TextView orderIdTextView,txnAmountTextView,bankTxnIdTextView,txnIdTextView,bankNameTextView;
    static String locationLatitude;
    static String locationLongitude;
    static int noOfBookingDays;
    static int noOfBookingHours;
    static int netAmount;
    RelativeLayout paymentLayout;
    TextView selectedVendorTextView,selectedVendorParkingAddress,noOfVehiclesWithSelectedVendor,noOfVehiclesOfVendorTextView;
    static TextView noOfVeh;
    RelativeLayout similarVehiclesLayout;
    static int pickUpYear,pickUpMonth,pickUpDay,pickUpHour,pickUpMinute;
    static int deliveryYear,deliveryMonth,deliveryDay,deliveryHour,deliveryMinute;
    static String pickUpString;
    static String deliveryString;
    static long bookingInterval;
    static long bookingHours;
    static String selectedUid;
    static String bookingVendor,bookingAddress,bookingUid;
    List<String> selectedUids;
    TextView PickUpText,DeliveryText;

    TextView OrderId, TxnAmount, BankTxnId, TxnId, BankName, BookingHours, BookingUserName, PickUp, Delivery;
    TextView balanceAmount, totalAmount ,payableAmount;
    TextView txnDate;
    TextView customerPhoneNumber, customerEmail;
    RelativeLayout discountLayout, couponLayout;
    TextView couponCode, discount;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_1);

        toolbar = (Toolbar) findViewById(R.id.toolbar_transparent);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        OrderId = (TextView) findViewById(R.id.orderId);
        TxnAmount = (TextView) findViewById(R.id.transactionAmount);
        BankTxnId = (TextView) findViewById(R.id.bankTxnId);
        TxnId = (TextView) findViewById(R.id.transactionId);
        BankName = (TextView) findViewById(R.id.bankName);
        BookingHours = (TextView) findViewById(R.id.bookingHours);
        BookingUserName = (TextView) findViewById(R.id.userName);
        PickUp = (TextView) findViewById(R.id.PickUp);
        Delivery = (TextView) findViewById(R.id.Delivery);
        balanceAmount = (TextView) findViewById(R.id.balanceAmount);
        totalAmount = (TextView) findViewById(R.id.totalAmount);
        txnDate = (TextView) findViewById(R.id.txnDateTextView);
        customerPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        customerEmail = (TextView) findViewById(R.id.email);
        discountLayout = (RelativeLayout) findViewById(R.id.discountLayout);
        couponLayout = (RelativeLayout) findViewById(R.id.couponLayout);
        discount = (TextView) findViewById(R.id.discount);
        couponCode = (TextView) findViewById(R.id.couponCode);
        payableAmount = (TextView)findViewById(R.id.payableAmount);

        locationLatitude = null;
        locationLongitude = null;

        selectedVendorTextView = (TextView)findViewById(R.id.vendorName);
        selectedVendorParkingAddress = (TextView)findViewById(R.id.vendorsParkingAddress);
        noOfVehiclesWithSelectedVendor = (TextView)findViewById(R.id.NoOfVehiclesWithThisVendor);
        noOfVehiclesOfVendorTextView = (TextView)findViewById(R.id.NoOfVehiclesWithThisVendorTextView);

        similarVehiclesLayout = (RelativeLayout)findViewById(R.id.similarVehiclesLayout);

        calendarLayout = (LinearLayout)findViewById(R.id.calendarLayout);
        paymentLayout = (RelativeLayout)findViewById(R.id.paymentLayout);
        nonBookingLayout = (LinearLayout)findViewById(R.id.nonBookingLayout);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(mCity + "/Bikes");

        // mVehicleNo = new ArrayList<String>();
        mVehicleNames = new ArrayList<String>();
        mVehicleLocations = new ArrayList<String>();
        mPricePerHour = new ArrayList<String>();
        mPricePerDay = new ArrayList<String>();
        mVehicleImages = new ArrayList<String>();
        VendorNames = new ArrayList<String>();
        noOfVehicles = new ArrayList<String>();
        mCity = new ArrayList<String>();
        vehicleType = new ArrayList<String>();
        orderId = new ArrayList<String>();
        txnAmount = new ArrayList<String>();
        bankTxnId = new ArrayList<String>();
        txnId = new ArrayList<String>();
        bankName = new ArrayList<String>();
        selectedUids = new ArrayList<String>();
        parkingAddresses = new ArrayList<ParkingAddress>();
        noOfVendors = new ArrayList<String>();

        pickUpDateTextView = (TextView)findViewById(R.id.pickUpDateTextView);
        deliveryDateTextView = (TextView)findViewById(R.id.deliverDateTextView);
        pickUpTime = (TextView)findViewById(R.id.pickUpTimeTextView);
        deliveryTime = (TextView)findViewById(R.id.deliveryTimeTextView);
        bookingNoOfVehiclesTextView = (TextView)findViewById(R.id.bookingNoOfVehicles);
        noOfVeh = (TextView)findViewById(R.id.noOfVeh);

        mVehicleImageView = (ImageView) findViewById(R.id.vehiclesImage) ;
        mVehicleName = (TextView) findViewById(R.id.vehiclesName) ;
        mVehicleLocation = (TextView) findViewById(R.id.vendorsParkingAddress);
        mVehicleCity = (TextView) findViewById(R.id.vehicleCity) ;
        mnetPayableAmount = (TextView) findViewById(R.id.finalCost);
        bookingDetailsLayout = (LinearLayout)findViewById(R.id.bookingDetailsLayout);
        orderIdTextView = (TextView)findViewById(R.id.orderId);
        txnAmountTextView = (TextView)findViewById(R.id.transactionAmount);
        bankTxnIdTextView = (TextView)findViewById(R.id.bankTxnId);
        txnIdTextView = (TextView)findViewById(R.id.transactionId);
        bankNameTextView = (TextView)findViewById(R.id.bankName);
        bookVehicleLayout = (LinearLayout)findViewById(R.id.bookVehicleLayout);
        PickUpText = (TextView)findViewById(R.id.PickUp);
        DeliveryText = (TextView)findViewById(R.id.Delivery);

        OYORecyclerView = (RecyclerView) findViewById(R.id.similar_oyo_recycler_view) ;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HotelActivity_1.this, Chat.class));
            }
        });

        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        SetDatesActivity.noOfVehicles = 1;

        Calendar c = Calendar.getInstance();

        pickUpYear = c.get(Calendar.YEAR);
        pickUpMonth = c.get(Calendar.MONTH) + 1;
        pickUpDay = c.get(Calendar.DAY_OF_MONTH);

        pickUpDateTextView.setText(pickUpDay + "-" + pickUpMonth + "-" + pickUpYear);

        pickUpHour = c.get(Calendar.HOUR_OF_DAY);
        pickUpMinute = c.get(Calendar.MINUTE);
        pickUpTime.setText(pickUpHour + ":" + pickUpMinute);
        pickUpString = pickUpYear + "-" + pickUpMonth + "-" + pickUpDay + " " + pickUpHour + ":" + pickUpMinute;

        c.add(Calendar.HOUR_OF_DAY,24);

        deliveryYear = c.get(Calendar.YEAR);
        deliveryMonth = c.get(Calendar.MONTH) + 1;
        deliveryDay = c.get(Calendar.DAY_OF_MONTH);

        deliveryDateTextView.setText(deliveryDay + "-" + deliveryMonth + "-" + deliveryYear);

        deliveryHour = c.get(Calendar.HOUR_OF_DAY);
        deliveryMinute = c.get(Calendar.MINUTE);
        deliveryTime.setText(deliveryHour + ":" + deliveryMinute);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        deliveryString = deliveryYear + "-" + deliveryMonth + "-" + deliveryDay + " " + deliveryHour + ":" + deliveryMinute;;

        try {
            Date pickUp = simpleDateFormat.parse(pickUpString);
            Date delivery = simpleDateFormat.parse(deliveryString);

            bookingInterval = delivery.getTime() - pickUp.getTime();

            bookingHours = bookingInterval/(60 * 60 * 1000);

            bookingNoOfVehiclesTextView.setText(bookingHours+"H");

            noOfBookingDays = (int) bookingHours/24;
            noOfBookingHours = (int) bookingHours%24;

            if (noOfBookingHours != 0){

                noOfBookingDays = noOfBookingDays + 1;
                Toast.makeText(this, "" + noOfBookingDays + " days", Toast.LENGTH_SHORT).show();

            }

            netAmount = (noOfBookingDays*Integer.parseInt(HotelAdapter.currentVehiclePricePerDay))*SetDatesActivity.noOfVehicles;

            mnetPayableAmount.setText(Integer.toString(netAmount));

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.get().load(HotelAdapter.currentVehicleImage).into(mVehicleImageView);
        mVehicleName.setText(HotelAdapter.currentVehicleName);
        mVehicleCity.setText(HotelAdapter.city);
        district = HotelAdapter.city;
        vehType = HotelAdapter.type;
        isAddedToFavourites = "false";

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        parkingAddressReference = mFirebaseDatabase.getReference(district + "/room/" + HotelAdapter.currentVehicleName);

        if (HotelAdapter.content == "Bookings"){

            nonBookingLayout.setVisibility(View.GONE);
            bookingDetailsLayout.setVisibility(View.VISIBLE);
            noOfVehiclesOfVendorTextView.setVisibility(View.GONE);

            //get location latitude and location longitude from bookings

            DatabaseReference locationReference = FirebaseDatabase.getInstance().getReference("Users/" + LoginActivity.userUid + "/Bookings");

            locationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot2 : dataSnapshot.getChildren()){

                        if (HotelAdapter.currentVehicleName.equals(snapshot2.child("VehicleName").getValue(String.class)) && HotelAdapter.city.equals(snapshot2.child("City").getValue(String.class)) && HotelAdapter.type.equals(snapshot2.child("VehicleType").getValue(String.class))){


                            locationLatitude = snapshot2.child("LocationLatitude").getValue(String.class);
                            locationLongitude = snapshot2.child("LocationLongitude").getValue(String.class);
                            selectedVendor = snapshot2.child("Vendor").getValue(String.class);
                            selectedAddress = snapshot2.child("ParkingAddress").getValue(String.class);
                            selectedUid = snapshot2.getKey();
                            noOfVehiclesWithVendor = snapshot2.child("BookedNoOfVehicles").getValue(String.class);

                            OrderId.setText(snapshot2.child("OrderId").getValue(String.class));
                            TxnAmount.setText(snapshot2.child("TxnAmount").getValue(String.class));
                            BankTxnId.setText(snapshot2.child("BankTxnId").getValue(String.class));
                            TxnId.setText(snapshot2.child("TxnId").getValue(String.class));
                            BankName.setText(snapshot2.child("BankName").getValue(String.class));
                            BookingHours.setText(snapshot2.child("BookedInterval").getValue(String.class) + " Hours");
                            BookingUserName.setText(snapshot2.child("Vendor").getValue(String.class));
                            PickUp.setText(snapshot2.child("PickUp").getValue(String.class));
                            Delivery.setText(snapshot2.child("Delivery").getValue(String.class));
                            txnDate.setText(snapshot2.child("TxnDate").getValue(String.class));
                            totalAmount.setText(snapshot2.child("TotalAmount").getValue(String.class));
                            balanceAmount.setText(snapshot2.child("BalanceAmount").getValue(String.class));
                            payableAmount.setText(snapshot2.child("PayableAmount").getValue(String.class));

                            if (snapshot2.hasChild("CouponCode")) {

                                discountLayout.setVisibility(View.VISIBLE);
                                couponLayout.setVisibility(View.VISIBLE);

                                discount.setText(snapshot2.child("Discount").getValue(String.class));
                                couponCode.setText(snapshot2.child("CouponCode").getValue(String.class));

                            }

                            String userUid = snapshot2.child("VendorUid").getValue(String.class);

                            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Vendors/" + userUid);

                            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    customerPhoneNumber.setText(dataSnapshot.child("PhoneNumber").getValue(String.class));

                                    customerEmail.setText(dataSnapshot.child("Email").getValue(String.class));


                                    selectedVendorTextView.setText(selectedVendor);
                                    selectedVendorParkingAddress.setText(selectedAddress);
                                    noOfVehiclesWithSelectedVendor.setText(noOfVehiclesWithVendor + " room booked");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{

            //calendarLayout.setVisibility(View.VISIBLE);
            paymentLayout.setVisibility(View.VISIBLE);
            bookingDetailsLayout.setVisibility(View.GONE);
            bookVehicleLayout.setVisibility(View.VISIBLE);
            similarVehiclesLayout.setVisibility(View.VISIBLE);

            parkingAddressReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    parkingAddresses.clear();
                    selectedUids.clear();

                    DataSnapshot snapshot = dataSnapshot.child("ParkingAddress");

                    for (DataSnapshot snapshot1 : snapshot.getChildren()){

                        if ("false".equals(snapshot1.child("isVehicleBlocked").getValue(String.class))){

                            ParkingAddress parkingAddress = new ParkingAddress();
                            parkingAddress.setVendorName(snapshot1.child("VendorName").getValue(String.class));
                            parkingAddress.setAddress(snapshot1.child("Address").getValue(String.class));
                            parkingAddress.setNoOfVehiclesWithCurrentVendor(snapshot1.child("NoOfVehicles").getValue(String.class));
                            Log.i("address",snapshot1.child("Address").getValue(String.class));
                            parkingAddresses.add(parkingAddress);

                            selectedUids.add(snapshot1.getKey());

                        }

                    }

                    Log.i("size","" + parkingAddresses.size());
                    if (parkingAddresses.size() != 1){
                        Log.i("no of radio buttons","" + parkingAddresses.size());
                        for (int i=0;i<parkingAddresses.size();i++){

                            if(i==0){

                                selectedVendor = parkingAddresses.get(0).getVendorName();
                                selectedAddress = parkingAddresses.get(0).getAddress();
                                selectedUid = selectedUids.get(0);
                                noOfVehiclesWithVendor = parkingAddresses.get(0).getNoOfVehiclesWithCurrentVendor();

                            }

                        }

                    }
                    else {

                        selectedVendor = parkingAddresses.get(0).getVendorName();
                        selectedAddress = parkingAddresses.get(0).getAddress();
                        selectedUid = selectedUids.get(0);
                        noOfVehiclesWithVendor = parkingAddresses.get(0).getNoOfVehiclesWithCurrentVendor();

                    }

                    selectedVendorTextView.setText(selectedVendor);
                    selectedVendorParkingAddress.setText(selectedAddress);
                    noOfVehiclesWithSelectedVendor.setText(noOfVehiclesWithVendor);

                    for (DataSnapshot snapshot1 : snapshot.getChildren()){

                        if (selectedAddress.equals(snapshot1.child("Address").getValue(String.class)) && selectedVendor.equals(snapshot1.child("VendorName").getValue(String.class))){

                            locationLatitude = snapshot1.child("LocationLatitude").getValue(String.class);
                            locationLongitude = snapshot1.child("LocationLongitude").getValue(String.class);

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        mDatabaseReference = mFirebaseDatabase.getReference(district + "/room");

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
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

                        if ("true".equals(snap1.child("isVehicleBlocked").getValue(String.class)) || "BlockedByAdmin".equals(snap1.child("isVehicleBlocked").getValue(String.class))){

                            //dont add the room
                        }
                        else {

                            if (HotelAdapter.content == "AllVehicles" || HotelAdapter.content == "Favourites" || HotelAdapter.content == "Bookings"){

                                mVehicleImages.add(chidSnap.child("VehiclePhoto").getValue(String.class));
                                mVehicleNames.add(chidSnap.getKey());
                                mPricePerHour.add(chidSnap.child("PricePerHour").getValue(String.class));
                                mPricePerDay.add(chidSnap.child("PricePerDay").getValue(String.class));
                                noOfVehicles.add(chidSnap.child("NoOfVehiclesAvailable").getValue(String.class));
                                mCity.add(district);
                                vehicleType.add(vehType);
                                orderId.add(null);
                                bankTxnId.add(null);
                                bankName.add(null);
                                txnAmount.add(null);
                                txnId.add(null);
                                noOfVendors.add(snap1.child("Address").getValue(String.class));

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

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
                        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                        linearLayoutManager1.setOrientation(linearLayoutManager.HORIZONTAL);

                        mOYOAdapter = new HotelAdapter(HotelActivity_1.this,mVehicleImages,mFavouriteImages,mCity,vehicleType,mVehicleNames,mPricePerHour,mPricePerDay,noOfVendors,noOfVehicles,orderId,txnAmount,bankTxnId,txnId,bankName);
                        OYORecyclerView.setAdapter(mOYOAdapter);
                        OYORecyclerView.setLayoutManager(linearLayoutManager1);

                        /*mPersonName = (TextView) findViewById(R.id.person_name);
                        mPersonName.setText( getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                .getString("name", "xyz"));*/

                       /* mPleaseReadTextView = (TextView) findViewById(R.id.please_read_text_view);
                        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.activit_back_in);
                        slide.setStartTime(5000);
                        mPleaseReadTextView.startAnimation(slide);*/

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(HotelActivity_1.this, "error loading data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showLocation(View view){

        Intent intent1 = new Intent(HotelActivity_1.this,ShowLocationOnMapActivity.class);
        startActivity(intent1);

    }

    public void displayImages(View view){

        /*Intent intent1 = new Intent(HotelActivity_1.this,DisplayImages.class);
        startActivity(intent1);*/

    }

    public void bookVehicleButton(View view){

        //change 2

        bookingUid = selectedUid;
        bookingVendor = selectedVendor;
        bookingAddress = selectedAddress;

        UserDetails.chatWith = selectedVendor;

        startActivity(new Intent(HotelActivity_1.this, Chat.class));

        /*

        if (Integer.parseInt(noOfVehiclesWithVendor) >= SetDatesActivity.noOfVehicles){

            if (bookingHours<24){

                Toast.makeText(this, "please select the booking interval correctly.Minimum booking interval is 24 hours.", Toast.LENGTH_SHORT).show();

            }
            else {

                Intent paymentIntent = new Intent(HotelActivity_1.this,PrePaymentPage.class);
                startActivity(paymentIntent);

            }

        }
        else {

            Toast.makeText(this, "Vendor does not have the required no of vehicles", Toast.LENGTH_SHORT).show();

        }*/

    }

    public void PickUpActivity(View view) {

        final Calendar c = Calendar.getInstance();

        pickUpYear = c.get(Calendar.YEAR);
        pickUpMonth = c.get(Calendar.MONTH);
        pickUpDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(HotelActivity_1.this,android.R.style.Theme_Holo_Dialog_MinWidth,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                pickUpYear = year;
                pickUpMonth = monthOfYear + 1;
                pickUpDay = dayOfMonth;

                pickUpHour = c.get(Calendar.HOUR_OF_DAY);
                pickUpMinute = c.get(Calendar.MINUTE);

                pickUpDateTextView.setText(pickUpDay + "-" + pickUpMonth + "-" + pickUpYear);

                TimePickerDialog timePickerDialog = new TimePickerDialog(HotelActivity_1.this,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        pickUpHour = hourOfDay;
                        pickUpMinute = minute;

                        pickUpTime.setText(pickUpHour + ":" + pickUpMinute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        pickUpString = pickUpYear + "-" + pickUpMonth + "-" + pickUpDay + " " + pickUpHour + ":" + pickUpMinute;

                        try {
                            Date pickUp = simpleDateFormat.parse(pickUpString);
                            Date delivery = simpleDateFormat.parse(deliveryString);

                            bookingInterval = delivery.getTime() - pickUp.getTime();

                            bookingHours = bookingInterval/(60 * 60 * 1000);

                            bookingNoOfVehiclesTextView.setText(bookingHours+"H");

                            noOfBookingDays = (int) bookingHours/24;
                            noOfBookingHours = (int) bookingHours%24;

                            if (noOfBookingHours != 0){

                                noOfBookingDays = noOfBookingDays + 1;
                                Toast.makeText(getApplicationContext(), "" + noOfBookingDays + " days", Toast.LENGTH_SHORT).show();

                            }

                            netAmount = (noOfBookingDays*Integer.parseInt(HotelAdapter.currentVehiclePricePerDay))*SetDatesActivity.noOfVehicles;


                            mnetPayableAmount.setText(Integer.toString(netAmount));

                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }, pickUpHour,pickUpMinute, true);

                timePickerDialog.show();

            }
        },pickUpYear,pickUpMonth,pickUpDay);

        datePickerDialog.show();
    }

    public void DeliveryActivity(View view){

        final Calendar c = Calendar.getInstance();

        c.add(Calendar.HOUR_OF_DAY,24);

        deliveryYear = c.get(Calendar.YEAR);
        deliveryMonth = c.get(Calendar.MONTH);
        deliveryDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(HotelActivity_1.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                deliveryYear = year;
                deliveryMonth = monthOfYear + 1;
                deliveryDay = dayOfMonth;

                deliveryHour = c.get(Calendar.HOUR_OF_DAY);
                deliveryMinute = c.get(Calendar.MINUTE);

                deliveryDateTextView.setText(deliveryDay + "-" + deliveryMonth + "-" + deliveryYear);

                TimePickerDialog timePickerDialog = new TimePickerDialog(HotelActivity_1.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        deliveryHour = hourOfDay;
                        deliveryMinute = minute;

                        deliveryTime.setText(deliveryHour + ":" + deliveryMinute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        deliveryString = deliveryYear + "-" + deliveryMonth + "-" + deliveryDay + " " + deliveryHour + ":" + deliveryMinute;

                        try {
                            Date pickUp = simpleDateFormat.parse(pickUpString);
                            Date delivery = simpleDateFormat.parse(deliveryString);

                            bookingInterval = delivery.getTime() - pickUp.getTime();

                            bookingHours = bookingInterval/(60 * 60 * 1000);

                            bookingNoOfVehiclesTextView.setText(bookingHours+"H");

                            noOfBookingDays = (int) bookingHours/24;
                            noOfBookingHours = (int) bookingHours%24;

                            if (noOfBookingHours != 0){

                                noOfBookingDays = noOfBookingDays + 1;
                                Toast.makeText(getApplicationContext(), "" + noOfBookingDays + " days", Toast.LENGTH_SHORT).show();

                            }

                            netAmount = (noOfBookingDays*Integer.parseInt(HotelAdapter.currentVehiclePricePerDay))*SetDatesActivity.noOfVehicles;


                            mnetPayableAmount.setText(Integer.toString(netAmount));

                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, deliveryHour, deliveryMinute, true);

                timePickerDialog.show();

            }
        },deliveryYear, deliveryMonth, deliveryDay);

        datePickerDialog.show();

    }

    public void selectNoOfVehicles(View view){

        /*MainActivity.isSearchActivity = false;

        Intent intent= new Intent(this,SetDatesActivity.class);
        startActivity(intent);*/
        //overridePendingTransition(R.anim.activit_back_in, R.anim.activity_back_out);

    }

}


