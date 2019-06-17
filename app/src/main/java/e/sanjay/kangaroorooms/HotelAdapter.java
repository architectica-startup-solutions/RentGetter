package e.sanjay.kangaroorooms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelItemViewHolder> {


    private List<Integer> mFavouriteImages;
    List<String> mVehicleNames;
    List<String> mNoOfVendors;
    List<String> mPricePerHour;
    List<String> mPricePerDay;
    List<String> mVehicleImages;
    List<String> noOfVehicles;
    List<String> orderId,txnAmount,bankTxnId,txnId,bankName;
    private LayoutInflater mInflater;
    private Context context;
    public int row_index;
    ProgressDialog pd;
    List<String> mCity;
    List<String> vehicleType;
    private int width;

    static String city;
    static String type;
    static String name;
    static String currentVehicleImage;
    static String currentVehiclePricePerHour;
    static String currentVehiclePricePerDay;
    static String currentVehicleName;
    static String NoOfVehiclesOfCurrentVehicle;
    static String NoOfVendorsOfCurrentVehicle;
    static String content;
    static String currentOrderId;
    static String currentTxnId;
    static String currentTxnAmount;
    static String currentBankTxnId;
    static String currentBankName;
    static String currentPickUp;
    static String currentDelivery;

    public class HotelItemViewHolder extends RecyclerView.ViewHolder {

        public final ImageView vehicleImageView;
        public final ImageView favouriteSymbol;
        final HotelAdapter mAdapter;
        public final TextView mVehicleName;
        public final TextView mVehicleCity;
        public final TextView mPricePerHour;
        public final TextView mPricePerDay;
        public final TextView mRoomAddress;
        public final LinearLayout mVehicleDetailsLayout;
        public final RelativeLayout descriptionLayout;


        public HotelItemViewHolder(View itemView, HotelAdapter adapter) {
            super(itemView);
            vehicleImageView = (ImageView) itemView.findViewById(R.id.vehicleImage);
            favouriteSymbol = (ImageButton) itemView.findViewById(R.id.favourite_symbol1);
            mVehicleName = (TextView) itemView.findViewById(R.id.vehicleName) ;
            mAdapter = adapter;
            mVehicleCity = (TextView) itemView.findViewById(R.id.city) ;
            mPricePerHour = (TextView) itemView.findViewById(R.id.pricePerHour);
            mPricePerDay = (TextView) itemView.findViewById(R.id.pricePerDay);
            mRoomAddress = (TextView) itemView.findViewById(R.id.noOfVendors);
            mVehicleDetailsLayout = (LinearLayout) itemView.findViewById(R.id.hotel_details);
            descriptionLayout = (RelativeLayout)itemView.findViewById(R.id.descriptionLayout);
        }
    }

    public HotelAdapter(Context context,List<String> mVehicleImages,List<Integer> mFavouriteImages,List<String> mCity,List<String> vehicleType,List<String> mVehicleNames,
                        List<String> mPricePerHour,  List<String> mPricePerDay,List<String> mNoOfVendors,List<String> noOfVehicles,List<String> orderId,List<String> txnAmount,List<String> bankTxnId,List<String> txnId,List<String> bankName) {
        mInflater = LayoutInflater.from(context);
        this.mVehicleImages = mVehicleImages;
        this.context = context;
        this.mFavouriteImages = mFavouriteImages;
        this.mCity = mCity;
        this.vehicleType = vehicleType;
        this.mVehicleNames = mVehicleNames;
        this.mPricePerHour = mPricePerHour;
        this.mPricePerDay = mPricePerDay;
        this.mNoOfVendors = mNoOfVendors;
        this.width = width;
        this.noOfVehicles = noOfVehicles;
        this.orderId = orderId;
        this.txnAmount = txnAmount;
        this.bankTxnId = bankTxnId;
        this.txnId = txnId;
        this.bankName = bankName;
    }
    @Override
    public HotelAdapter.HotelItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.hotels_list_item, parent, false);
        return new HotelAdapter.HotelItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(final HotelAdapter.HotelItemViewHolder holder, final int position) {

        final String mCurentVehicleImage = mVehicleImages.get(position);
        final int mCurrentFavouriteImage = mFavouriteImages.get(position);
        final String mCurrentVehicleName = mVehicleNames.get(position) ;
        final String mCurrentVehiclePricePerHour = mPricePerHour.get(position);
        final String mCurrentVehiclePricePerDay = mPricePerDay.get(position);
        final String mCurrentRoomAddress = mNoOfVendors.get(position);
        final String mCurrentNoOfVehicles = noOfVehicles.get(position);
        final String mCurrentCity = mCity.get(position);
        final String mCurrentVehType = vehicleType.get(position);


        Picasso.get().load(mCurentVehicleImage).into(holder.vehicleImageView);
        holder.mVehicleCity.setText(mCurrentCity);
        holder.mVehicleName.setText(mCurrentVehicleName);
        holder.mPricePerHour.setText(mCurrentVehiclePricePerHour);
        holder.mPricePerDay.setText(mCurrentVehiclePricePerDay);
        holder.mRoomAddress.setText(mCurrentRoomAddress);

        if (content == "Bookings"){

            holder.favouriteSymbol.setVisibility(View.GONE);

        }
        else if (content == "Favourites"){

            holder.favouriteSymbol.setVisibility(View.VISIBLE);
            holder.favouriteSymbol.setImageResource(mCurrentFavouriteImage);

        }
        else if (content == "ViewVehicles"){

            holder.favouriteSymbol.setVisibility(View.VISIBLE);

            holder.favouriteSymbol.setImageResource(mCurrentFavouriteImage);
        }
        else if (content == "AllVehicles"){

            holder.favouriteSymbol.setVisibility(View.VISIBLE);

            holder.favouriteSymbol.setImageResource(mCurrentFavouriteImage);

        }

        holder.favouriteSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFavouriteImages.get(position) == R.drawable.ic_favorite_white_24dp) {

                    //add to favourites

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("VehiclePhoto",mCurentVehicleImage);
                    map.put("PricePerHour",mCurrentVehiclePricePerHour);
                    map.put("PricePerDay",mCurrentVehiclePricePerDay);
                    map.put("RoomAddress",mCurrentRoomAddress);
                    map.put("VehicleName",mCurrentVehicleName);
                    map.put("City",mCurrentCity);

                    Firebase.setAndroidContext(context);
                    Firebase favouritesReference = new Firebase("https://kangaroorooms-288e3.firebaseio.com/Users/" + LoginActivity.userUid + "/Favourites" );

                    favouritesReference.push().setValue(map);

                    notifyDataSetChanged();

                    Toast.makeText(context, "Room added to favourites", Toast.LENGTH_SHORT).show();
                }
                else if(mFavouriteImages.get(position) == R.drawable.ic_favorite_red_24dp){

                    //delete from favourites

                    DatabaseReference favourites = FirebaseDatabase.getInstance().getReference("Users/" + LoginActivity.userUid + "/Favourites");
                    favourites.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                if (snapshot.child("VehicleName").getValue(String.class).equals(mCurrentVehicleName) && snapshot.child("City").getValue(String.class).equals(mCurrentCity)) {
                                    String favouritesUid = snapshot.getKey();
                                    DatabaseReference removeFavourites = FirebaseDatabase.getInstance().getReference("Users/" + LoginActivity.userUid + "/Favourites/" + favouritesUid);
                                    removeFavourites.removeValue();
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    notifyDataSetChanged();

                    Toast.makeText(context, "Room removed from favourites", Toast.LENGTH_SHORT).show();

                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                city = mCurrentCity;
                name = mCurrentVehicleName;
                type = mCurrentVehType;
                currentVehicleImage = mCurentVehicleImage;
                currentVehiclePricePerHour = mCurrentVehiclePricePerHour;
                currentVehiclePricePerDay = mCurrentVehiclePricePerDay;
                currentVehicleName = mCurrentVehicleName;
                NoOfVehiclesOfCurrentVehicle = mCurrentNoOfVehicles;
                //NoOfVendorsOfCurrentVehicle = mCurrentNoOfVendors;
                currentOrderId = orderId.get(position);
                currentBankName = bankName.get(position);
                currentBankTxnId = bankTxnId.get(position);
                currentTxnAmount = txnAmount.get(position);
                currentTxnId = txnId.get(position);

                Intent intent = new Intent(context, HotelActivity_1.class);
                (context).startActivity(intent);

                /*if (content == "Bookings"){

                    currentPickUp = Bookings.pickUpStrings.get(position);
                    currentDelivery = Bookings.deliveryStrings.get(position);
                }

                if (mCurrentNoOfVehicles == "0"){

                    Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show();

                }
                else {

                    Intent intent = new Intent(context, HotelActivity_1.class);
                    (context).startActivity(intent);

                }*/

            }

        });
    }

    @Override
    public int getItemCount() {
        return mVehicleImages.size();
    }

}
