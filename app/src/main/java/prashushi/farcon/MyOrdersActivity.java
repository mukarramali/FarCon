package prashushi.farcon;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Dell User on 7/23/2016.
 */
public class MyOrdersActivity extends AppCompatActivity {

    RecyclerView recycler_orders;
    LinearLayoutManager mLayoutManager;
    SharedPreferences sPrefs;
    boolean[] thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        init();
        thread = new boolean[]{true};
        sPrefs = getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);

        RecyclerAdapterOrders recyclerAdapter = new RecyclerAdapterOrders(MyOrdersActivity.this, new JSONArray(), thread);
        recycler_orders.setAdapter(recyclerAdapter);
        callServer();
    }

    private void callServer() {
        String id = sPrefs.getString("id", "0");
        String url = getString(R.string.local_host) + "orderprofile";

        ArrayList<String> params = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        //9808185808    8958050840
        params.add("id");
        values.add(id);
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        new BackgroundTaskPost(this, url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                /*output="[{\"total_amt\"=120, \n" +
                        "\"payment_mode\"=\"\",\n" +
                        "\"date_of_order\"=\"02-02-2016\",\n" +
                        "\"order_status\"=\"Delivered\",\n" +
                        "\"tracking_id\"=\"ORN/OT/15452\",\n" +
                        "\"item_count\"=3},\n" +
                        "{\"total_amt\"=150, \n" +
                        "\"payment_mode\"=\"\",\n" +
                        "\"date_of_order\"=\"02-02-2016\",\n" +
                        "\"order_status\"=\"Cancelled\",\n" +
                        "\"tracking_id\"=\"ORN/OT/15452\",\n" +
                        "\"item_count\"=2},\n" +
                        "{\"total_amt\"=100, \n" +
                        "\"payment_mode\"=\"\",\n" +
                        "\"date_of_order\"=\"02-02-2016\",\n" +
                        "\"order_status\"=\"Processed\",\n" +
                        "\"tracking_id\"=\"ORN/OT/15452\",\n" +
                        "\"item_count\"=3}]";
               */
                System.out.println(output);
                if (output.contains("error")) {
                    new Utilities().checkIfLogged(output, MyOrdersActivity.this);
                    findViewById(R.id.tv_empty_orders).setVisibility(View.VISIBLE);
                    return;
                } else if (output.contains("falsexxx")) {
                    findViewById(R.id.tv_empty_orders).setVisibility(View.VISIBLE);
                } else {
                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(output);
                        if (jsonArray.length() > 0) {
                            findViewById(R.id.tv_empty_orders).setVisibility(View.INVISIBLE);
                            RecyclerAdapterOrders recyclerAdapter = new RecyclerAdapterOrders(MyOrdersActivity.this, jsonArray, thread);
                            recycler_orders.setAdapter(recyclerAdapter);
                        } else
                            findViewById(R.id.tv_empty_orders).setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        //findViewById(R.id.tv_empty_orders).setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }
            }

        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.orders));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//      setupUI();
        recycler_orders = (RecyclerView) findViewById(R.id.recycler_orders);
        mLayoutManager = new LinearLayoutManager(this);
        recycler_orders.setLayoutManager(mLayoutManager);
        findViewById(R.id.tv_empty_orders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
