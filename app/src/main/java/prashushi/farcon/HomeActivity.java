package prashushi.farcon;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , KeyEvent.Callback, ViewPager.OnPageChangeListener, ListPackageDialog.OnCompleteListener {

    ViewPager viewPager;
    TabsPagerAdapter mAdapter;
    ActionBar actionBar;
    String[] tabs = { "Select", "Combos", "Quote" };
    TabLayout tabLayout;
    int fab_flag;
    Snackbar snackbar;
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    DBHelper mydb;
    FloatingActionButton fab;
    QuoteFragment quote;
    ShopFragment shop;
    ComboFragment combo;
    int count=0;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mydb=new DBHelper(this);
        sPrefs=getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        editor=sPrefs.edit();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_flag=1;
        search = (SearchView) findViewById( R.id.search);

        fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                fabFunction(view);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        search.setOnQueryTextListener(listener);
        setTabs();
    }
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            query = query.toLowerCase();

/*            mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            mAdapter = new Adapter(filteredList, MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();  // data set changed

  */          return true;

        }
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

    private void fabFunction(View view) {
        doIncrease();
        if(fab_flag==1) {

            setFabImage(R.drawable.close);
            int n= 0;
            float cost=0;
            try{
                n=mydb.numberOfRows();
                cost=mydb.getTotalCost();

            }catch (Exception e){
                e.printStackTrace();
            }
            String st="";
            if(n>0)
                st=n+" Item(s) \nTotal: Rs."+cost;
            else st="Cart Empty";

            snackbar=Snackbar.make(view, st, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null);
            snackbar.show();

            fab_flag=0;
        }
        else if(fab_flag==0){
            setFabImage(R.drawable.price);
            snackbar.dismiss();
            fab_flag=1;
        }
        else{
            System.out.println("***");
            sendQuote();

        }

    }

    private void doIncrease() {
        count++;
        invalidateOptionsMenu();

    }


    private void sendQuote() {

        if(quote!=null){
            View v=quote.getView();
            try {
                LinearLayout container = (LinearLayout) v.findViewById(R.id.layout_container);
                int n = container.getChildCount();
                JSONArray jsonArray = new JSONArray();
                JSONObject object;
                for (int i = 0; i < n; i++) {
                    object = new JSONObject();
                    LinearLayout child = (LinearLayout) container.getChildAt(i);
                    EditText etItem = (EditText) child.getChildAt(0);
                    EditText etQty = (EditText) child.getChildAt(1);
                    String item = etItem.getText().toString();
                    object.put("item_name", item);
                    String qty = etQty.getText().toString();
                    object.put("item_qty", qty);
                    if(item.length()==0) {
                        etItem.setError(getString(R.string.left_empty));
                        etItem.requestFocus();
                        return;
                    }

                    if(qty.length()==0) {
                        etQty.setError(getString(R.string.left_empty));
                        etQty.requestFocus();
                        return;
                    }

                    System.out.println("Item:" + item + ", Qty:" + qty);
                    jsonArray.put(i, object);
                }
                String item=jsonArray.toString();
                String id=sPrefs.getString("id", "0");
                Intent intent=new Intent(this, QuoteFormActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("item", item);

                startActivity(intent);

            }catch (JSONException e){

            }

        }

    }

    void setTabs(){
        // Initilization

        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(mAdapter.getCount()-1);
        actionBar.setHomeButtonEnabled(false);



        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(mAdapter);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



        int[] icons=new int[]{R.drawable.veg, R.drawable.combo, R.drawable.quote};
        String[] titles=new String[]{"Shop","Combo","Quote"};
        setupTabIcons(icons, titles);
        }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home, menu);
       // MenuItem menuItem = menu.findItem(R.id.testAction);
       // menuItem.setIcon(buildCounterDrawable(count, R.drawable.cart_white));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_cart:
                startActivity(new Intent(this, CartActivity.class));
                break;
            case R.id.action_logout:
                editor.clear();
                editor.commit();
                mydb.deleteDB();
                if(sPrefs.contains("id")&&sPrefs.contains("phone"))
                {
                    System.out.println("Home Activity-> id:"+sPrefs.getString("id", "")+", phone:"+sPrefs.getString("phone", ""));
                }
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.nav_referral) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_msg)+getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE).getString("referral_code", "0"));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getString(R.string.share_through)));  } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(this, FeedbackActivity.class));
        } else if (id == R.id.nav_visit){
            Uri uri = Uri.parse("http://www.farcon.in"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setupTabIcons(int[] icons, String[] title) {

        for(int i=0;i<icons.length;i++) {
            LinearLayout ll= (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
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

        for(int i=0;i<=2;i++) {
         if(i==position) {
            // LinearLayout ll= (LinearLayout)tabLayout.getTabAt(i).getCustomView();
             TextView tab = (TextView) tabLayout.getTabAt(i).getCustomView();

            // tab.setTextColor(getResources().getColor(R.color.colorAccent));
             tab.setTypeface(null, Typeface.BOLD);
         }
            else{
           //  LinearLayout ll= (LinearLayout)
             TextView tab = (TextView)tabLayout.getTabAt(i).getCustomView();
             //tab.setTextColor(getResources().getColor(R.color.white));

             tab.setTypeface(null, Typeface.NORMAL);
            }
         }
        switch (position){
            case 0:


            case 1:
                setFabImage(R.drawable.price);
                if(snackbar!=null)
                    snackbar.dismiss();
                fab_flag=1;
                break;
            case 2:
                setFabImage(R.drawable.approve);
                if(snackbar!=null)
                    snackbar.dismiss();
                fab_flag=3;

                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onComplete(String id, String size, String price, int pos) {
        System.out.println("in home activity");
        View v=shop.recyclerview_shop.findViewHolderForLayoutPosition(pos).itemView;
    printSizePrice(v, price, size);
    }
    private void printSizePrice(View clickedItem, String price1, String size1) {
        TextView item_costTv= (TextView) clickedItem.findViewById(R.id.tv_price);
        item_costTv.setText(price1+" "+getResources().getString(R.string.Rs));

        TextView item_sizeTv= (TextView) clickedItem.findViewById(R.id.tv_spinner);
        item_sizeTv.setText(getSizeTag(Double.valueOf(size1)));
    }
    private String getSizeTag(Double size_d) {
        String tag="Kg";
        if(size_d<1.0)
        {
            size_d*=1000;
            tag="grams";
        }
        return size_d+" "+tag;
    }



    class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:
                    // Top Rated fragment activity
                    shop=new ShopFragment();
                    return shop;
                case 1:
                    // Games fragment activity
                    combo=new ComboFragment();
                    return combo;
                case 2:
                    // Movies fragment activity
                    quote=new QuoteFragment();
                    return quote;
            }

            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 3;
        }

    }

    private void setFabImage(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setImageDrawable(getResources().getDrawable(id, getTheme()));
        } else {
            fab.setImageDrawable(getResources().getDrawable(id));
        }

    }


/*    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        System.out.println("here");
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());


        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        System.out.println("again");
        return new BitmapDrawable(getResources(), bitmap);
    }
*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            finish();
            System.exit(0);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            finish();
            System.exit(0);
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

}
