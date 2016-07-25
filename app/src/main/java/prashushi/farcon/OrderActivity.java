package prashushi.farcon;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dell User on 7/23/2016.
 */
public class OrderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    ViewPager viewPager;
    TabsPagerAdapter mAdapter;
    //    ActionBar actionBar;
    TabLayout tabLayout;
    int fab_flag;
    Snackbar snackbar;
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    OrderSummaryFragment summary_fragment;
    OrderItemsFragment items_fragment;
    String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setTabs();
        View v = findViewById(R.id.header);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            v.setTransitionName("header");
            Slide slide = new Slide();
            slide.setDuration(1000);
            getWindow().setSharedElementEnterTransition(slide);
            getWindow().setSharedElementExitTransition(slide);
        }

        setHeader();

    }

    private void setHeader() {
        String date = "", id = "", amount_pay = "", items_count = "", order_status = "";
        int flag = 0;//0:not delivered, 1:processed, 2:delivered, 3:cancelled
        try {
            JSONObject header = new JSONObject(getIntent().getExtras().getString("order"));
            id = header.optString("id");
            order_id = id;
            date = header.optString("date_of_order");
            amount_pay = header.optString("total_amt");
            items_count = header.optString("item_count");
            order_status = header.optString("order_status").toLowerCase();
            if (order_status.contains("cancel"))
                flag = 3;
            else if (order_status.contains("deliver"))
                flag = 2;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        TextView tv_date_placed = (TextView) findViewById(R.id.tv_date);
        TextView tv_tracking_id = (TextView) findViewById(R.id.tv_id);
        TextView tv_amount = (TextView) findViewById(R.id.tv_amount);
        TextView tv_no_items = (TextView) findViewById(R.id.tv_items);
        TextView tv_status = (TextView) findViewById(R.id.tv_status);

        tv_date_placed.setText("Placed:" + date);
        tv_tracking_id.setText("Order ID:" + id);
        tv_amount.setText("Amount Payable:" + amount_pay);
        tv_no_items.setText(items_count + " Item(s)");
        tv_status.setText(capitalize(order_status));

     /*   RelativeLayout ball1= (RelativeLayout) findViewById(R.id.fab1);
        RelativeLayout ball2= (RelativeLayout) findViewById(R.id.fab2);
        RelativeLayout ball3= (RelativeLayout) findViewById(R.id.fab3);
        View rod1=findViewById(R.id.rod1);
        View rod2=findViewById(R.id.rod2);

        //0:not delivered, 1:processed, 2:delivered, 3:cancelled
        switch (flag){
            case 0:
                ball1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_green));
                ball2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_grey));
                ball3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_grey));
                rod1.setBackgroundColor(getResources().getColor(R.color.green_dark));
                rod2.setBackgroundColor(getResources().getColor(R.color.greydark));
                break;
            case 1:
                break;
            case 2:
                ball1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_green));
                ball2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_green));
                ball3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_green));
                rod1.setBackgroundColor(getResources().getColor(R.color.green_dark));
                rod2.setBackgroundColor(getResources().getColor(R.color.green_dark));

                break;
            case 3:
                ball1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_red));
                ball2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_grey));
                ball3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_grey));
                rod1.setBackgroundColor(getResources().getColor(R.color.greydark));
                rod2.setBackgroundColor(getResources().getColor(R.color.greydark));
                break;
        }
        */
    }

    private String capitalize(String name) {
        String st = name;
        if (name.length() == 0)
            return name;
        if (name.charAt(0) > 'Z') {
            st = (char) (name.charAt(0) - ('a' - 'A')) + name.substring(1);
        }
        return st;
    }

    void setTabs() {
        // Initilization

        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(mAdapter.getCount() - 1);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(mAdapter);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        int[] icons = new int[]{R.drawable.veg, R.drawable.combo, R.drawable.quote};
        String[] titles = new String[]{"Order Summary", "Items"};
        setupTabIcons(icons, titles);
    }

    private void setupTabIcons(int[] icons, String[] title) {

        for (int i = 0; i < title.length; i++) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tab = (TextView) ll.getChildAt(0);
            tab.setText(title[i]);
            //    tab.setCompoundDrawablesWithIntrinsicBounds(0, icons[i], 0, 0);
            tabLayout.getTabAt(i).setCustomView(tab);
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        //invalidateOptionsMenu();
        for (int i = 0; i <= 1; i++) {
            if (i == position) {
                // LinearLayout ll= (LinearLayout)tabLayout.getTabAt(i).getCustomView();
                TextView tab = (TextView) tabLayout.getTabAt(i).getCustomView();

                // tab.setTextColor(getResources().getColor(R.color.colorAccent));
                tab.setTypeface(null, Typeface.BOLD);
            } else {
                //  LinearLayout ll= (LinearLayout)
                TextView tab = (TextView) tabLayout.getTabAt(i).getCustomView();
                //tab.setTextColor(getResources().getColor(R.color.white));

                tab.setTypeface(null, Typeface.NORMAL);
            }
        }
        switch (position) {
            case 0:
                fab_flag = 0;
                break;

            case 1:
                fab_flag = 1;
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            Bundle args = new Bundle();
            args.putString("order_id", order_id);
            args.putAll(getIntent().getExtras());
            switch (index) {
                case 0:
                    summary_fragment = new OrderSummaryFragment();
                    summary_fragment.setArguments(args);
                    return summary_fragment;
                case 1:
                    items_fragment = new OrderItemsFragment();
                    items_fragment.setArguments(args);
                    return items_fragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 2;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }
}
