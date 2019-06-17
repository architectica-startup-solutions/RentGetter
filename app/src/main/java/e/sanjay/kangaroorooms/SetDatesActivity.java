package e.sanjay.kangaroorooms;

import android.graphics.Color;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static e.sanjay.kangaroorooms.HotelActivity_1.netAmount;
import static e.sanjay.kangaroorooms.HotelActivity_1.noOfBookingDays;

public class SetDatesActivity extends AppCompatActivity {

    String startDate;
    String endDate;
    int startDayNumber;
    int endDayNumber;
    String startDay;
    String endDay;
    TabItem tab3;
    TextView mRoomTab;
    public static TabLayout tabLayout;
    static int noOfVehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_dates);

        // Create an instance of the tab layout from the view.
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // Set the text for each tab.
        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(Color.BLACK,Color.RED);
        tabLayout.setSelectedTabIndicatorColor(Color.RED);
        // Using PagerAdapter to manage page views in fragments.
        // Each page is represented by its own fragment.
        // This is another aryan of the adapter pattern.
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        // Setting a listener for clicks.
        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        startDate = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(today);
        endDate = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(tomorrow);

        startDayNumber = today.getDay();
        endDayNumber = today.getDay();

        startDay = getDay(startDayNumber);
        endDay = getDay(endDayNumber);

        tab3 = (TabItem) findViewById(R.id.tab_3);

        //tabLayout.getTabAt(0).setText(startDay+", "+startDate);
        //tabLayout.getTabAt(1).setText(endDay+", "+endDate);
        mRoomTab = (TextView) LayoutInflater.from(this).inflate(R.layout.room_tab_custom_layout, null);
        tabLayout.getTabAt(0).setCustomView(mRoomTab);

    }

    public static String getDay(int Number) {
        switch(Number) {
            case 0:
                return "Sun";
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thu";
            case 5:
                return "Fri";
            case 6:
                return "Sat";
            default:
                return null;
        }
    }



    public void Apply(View view) {
        //SearchActivity1.mCheckInTextView.setText(tabLayout.getTabAt(0).getText());
        //SearchActivity1.mCheckOutTextView.setText(tabLayout.getTabAt(1).getText());

        noOfVehicles = Integer.parseInt(tabLayout.getTabAt(0).getText().toString());

        HotelActivity_1.noOfVeh.setText("NoOfRooms: " + noOfVehicles);

        netAmount = (noOfBookingDays*Integer.parseInt(HotelAdapter.currentVehiclePricePerDay))*SetDatesActivity.noOfVehicles;

        HotelActivity_1.mnetPayableAmount.setText(Integer.toString(netAmount));

        /*long diff=  CheckOutfragment.endDate.getTimeInMillis() -CheckInFragment.startDate.getTimeInMillis() ;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        diffDays = (diffDays == 0) ? 1: diffDays ;
        SearchActivity1.mNoOfNightsTextView.setText(diffDays+"N");*/
        //overridePendingTransition(R.anim.activit_back_in, R.anim.activity_back_out);
        finish();
    }
}

