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

}
