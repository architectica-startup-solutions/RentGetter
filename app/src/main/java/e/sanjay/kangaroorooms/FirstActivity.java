package e.sanjay.kangaroorooms;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static e.sanjay.kangaroorooms.LoginActivity.userUid;

public class FirstActivity extends AppCompatActivity {

    String name;
    public static BottomNavigationView bottomNavigationView;
    Fragment myFragment;
    Fragment selectedFragment;
    static String Vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        selectedFragment = HomeFragment.newInstance();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnav);

        bottomNavigationView.setItemIconTintList(null);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectedFragment = HomeFragment.newInstance();
                        switch (item.getItemId()) {
                            case R.id.home_menu_item:
                                selectedFragment = HomeFragment.newInstance();
                                break;
                            case R.id.saved_hotel_menu_item:
                                selectedFragment = SavedHotelFragment.newInstance();
                                break;
                            case R.id.bookings_menu_item:
                                selectedFragment = Bookings.newInstance();
                                break;
                            case R.id.you_menu_item:
                                selectedFragment = YouFragment.newInstance();
                                break;
                        }
                        myFragment = (Fragment) getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT");
                        if(!(myFragment.getClass().equals(selectedFragment.getClass())) ) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, selectedFragment,"CURRENT_FRAGMENT");
                            transaction.commit();
                        }
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance(),"CURRENT_FRAGMENT");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(!((HomeFragment.newInstance()).getClass().equals(selectedFragment.getClass())) ) {
            View view = FirstActivity.bottomNavigationView.findViewById(R.id.home_menu_item);
            view.performClick();
        }
        else {

            finish();

        }

    }

    public void showFamilyRooms(View view){

        Vehicle = "Tag1";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);

    }

    public void showSingleRooms(View view){

        Vehicle = "Tag0";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);

    }

    public void showBoysRooms(View view){

        Vehicle = "Tag2";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);

    }

    public void showGirlsRooms(View view){

        Vehicle = "Tag3";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);

    }

    public void showBoysHostels(View view){

        Vehicle = "Tag4";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);

    }

    public void showGirlsHostels(View view){

        Vehicle = "Tag5";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);

    }

    /*
    public void showSearchActivityBikes(View view) {
        Vehicle = "Bikes";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);
    }

    public void showSearchActivitySelfDriveCars(View view) {
        Vehicle = "Self drive cars";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);
    }

    public void showSearchActivityDriverDriveCars(View view) {
        Vehicle = "Cars with drivers";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);
    }

    */

    public void showSearchActivityAll(View view){

        Vehicle = "All";
        Intent intent = new Intent(this,SearchActivity1.class);
        intent.putExtra("vehicle",Vehicle);
        startActivity(intent);
    }

    public void showSearchActivity(View view){
        MenuItem homeItem = bottomNavigationView.getMenu().getItem(0);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance(),"CURRENT_FRAGMENT");
        transaction.commit();

        bottomNavigationView.getMenu().findItem(homeItem.getItemId()).setChecked(true);
    }

    public void openWhatsApp(View view){

        String number = "+91 8209595522";
        String url = "https://api.whatsapp.com/send?phone="+number;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    public void ShowCoupons(View view){

        Intent intent = new Intent(FirstActivity.this,CouponsActivity.class);
        startActivity(intent);

    }

}
