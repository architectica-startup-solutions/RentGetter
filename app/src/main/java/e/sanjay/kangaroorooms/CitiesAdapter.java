package e.sanjay.kangaroorooms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CityViewHolder> {


    private List<String> mCityNames;
    private LayoutInflater mInflater;
    private Context context;


    public class CityViewHolder extends RecyclerView.ViewHolder {

        public final TextView cityNameItemView;
        //public final ImageView wordImageView;
        final CitiesAdapter mAdapter;

        public CityViewHolder(View itemView, CitiesAdapter adapter) {
            super(itemView);
            cityNameItemView = (TextView) itemView.findViewById(R.id.city_name);
            mAdapter = adapter;
        }
    }
    public CitiesAdapter(Context context, List<String> mCityNames) {
        mInflater = LayoutInflater.from(context);
        this.mCityNames = mCityNames;
        this.context = context;
        //this.mWordImageList = imageList;
    }

    @Override
    public CitiesAdapter.CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.cities_list_item, parent, false);
        return new CitiesAdapter.CityViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(CitiesAdapter.CityViewHolder holder, int position) {
        final String mCurrentCityName = mCityNames.get(position);
        holder.cityNameItemView.setText(mCurrentCityName);
        holder.cityNameItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SearchActivity1.vehType.equals("All")){

                    Intent intent = new Intent(context,AllVehiclesActivity.class);
                    intent.putExtra(SearchActivity1.EXTRA_MESSAGE, mCurrentCityName);
                    ((Activity)context).startActivity(intent);

                }
                else {

                    Intent intent = new Intent(context,HotelsActivity.class);
                    intent.putExtra(SearchActivity1.EXTRA_MESSAGE, mCurrentCityName);
                    intent.putExtra(SearchActivity1.EXTRA_VEHICLE,SearchActivity1.vehType);
                    ((Activity)context).startActivity(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCityNames.size();
    }

}
