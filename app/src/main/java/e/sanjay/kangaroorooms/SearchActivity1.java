package e.sanjay.kangaroorooms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity1 extends AppCompatActivity {

    private Toolbar toolbar;
    List<String> cityNames;
    RecyclerView mCitiesRecyclerView;
    CitiesAdapter mAdapter;
    public SearchView search;
    private List<String> list = new ArrayList<String>();
    public static String vehType;
    ProgressDialog pd;
    public static final String EXTRA_MESSAGE =
            "com.aryan.android.oyo.extra.MESSAGE";
    public static final String EXTRA_VEHICLE = "com.aryan.android.oyo.extra.VEHICLE";

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
        setContentView(R.layout.activity_search1);

        toolbar = (Toolbar) findViewById(R.id.search_toolbar1);
        cityNames = new ArrayList<String>();
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        pd = new ProgressDialog(SearchActivity1.this);
        pd.setMessage("Loading...");
        pd.show();
        //cityNames = Arrays.asList(getResources().getStringArray(R.array.places_in_india));

        DatabaseReference cityNamesReference = FirebaseDatabase.getInstance().getReference();

        cityNamesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                cityNames.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    if ("Users".equals(dataSnapshot1.getKey()) || "Vendors".equals(dataSnapshot1.getKey()) || "Bookings".equals(dataSnapshot1.getKey()) || "Admins".equals(dataSnapshot1.getKey()) || "Coupons".equals(dataSnapshot1.getKey())){

                        //do nothing

                    }
                    else {

                        cityNames.add(dataSnapshot1.getKey());

                    }

                }

                pd.dismiss();

                search = (SearchView) findViewById(R.id.search_view) ;

                mCitiesRecyclerView = (RecyclerView) findViewById(R.id.cities_recycler_view);
                mAdapter = new CitiesAdapter(SearchActivity1.this,cityNames);
                mCitiesRecyclerView.setAdapter(mAdapter);
                mCitiesRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity1.this));

                Intent intent = getIntent();
                vehType = intent.getStringExtra("vehicle");
                Log.i("veh",vehType);

                SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        query = query.toLowerCase();
                        final List<String> filteredList = new ArrayList<>();
                        for(int i = 0; i < cityNames.size(); i++) {
                            final String text = cityNames.get(i).toLowerCase();
                            if (text.contains(query)) {
                                filteredList.add(cityNames.get(i));
                            }
                        }
                        mCitiesRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity1.this));
                        mAdapter = new CitiesAdapter(SearchActivity1.this,filteredList);
                        mCitiesRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        return true;
                    }
                };

                search.setOnQueryTextListener(listener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                pd.dismiss();
            }
        });


    }

}

