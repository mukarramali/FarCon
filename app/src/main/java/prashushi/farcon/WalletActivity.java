package prashushi.farcon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import com.victor.loading.newton.NewtonCradleLoading;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recycler_items;
    LinearLayoutManager mLayoutManager;
    SharedPreferences sPrefs;
    boolean[] thread;
    NewtonCradleLoading newtonCradleLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        init();
        newtonCradleLoading = (NewtonCradleLoading) findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.setVisibility(View.VISIBLE);
        newtonCradleLoading.setLoadingColor(getResources().getColor(R.color.colorPrimary));
        thread = new boolean[]{true};
        sPrefs = getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        callServer();
    }

    private void callServer() {
        String url = getString(R.string.local_host) + "callwallet";
        String id = sPrefs.getString("id", "0");
        newtonCradleLoading.start();

        ArrayList<String> params = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        //9808185808    8958050840
        params.add("id");
        values.add(id);
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        System.out.println("_token:" + sPrefs.getString("access_token", "0"));
        new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                newtonCradleLoading.stop();
                System.out.println(output);
             /*   output="[{\"referral_id\":1,\"name\":\"aloo\",\"item_qty\":3,\"item_combo\":0,\"item_count\":2}," +
                        "{\"referral_id\":1,\"name\":\"combo\",\"combo_qty\":1,\"item_combo\":1,\"" +
                                "items\":[{\"id\":1,\"item_id\":1,\"item_name\":\"Aaloo\",\"item_qty\":22},{\"id\":2,\"item_id\":1,\"item_name\":\"Onion\",\"item_qty\":12}],\"item_count\":1}]";
              */
                if (output.contains("error")) {
                    new Utilities().checkIfLogged(output, WalletActivity.this);
                    findViewById(R.id.tv_empty_wallet).setVisibility(View.VISIBLE);
                    return;
                } else if (output.contains("falsexxx")) {
                    findViewById(R.id.tv_empty_wallet).setVisibility(View.VISIBLE);
                } else {
                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(output);
                        if (jsonArray.length() > 0) {
                            findViewById(R.id.layout_see_offers).setVisibility(View.VISIBLE);
                            RecyclerAdapterWalletItems recyclerAdapterWalletItemsItem = new RecyclerAdapterWalletItems(WalletActivity.this, jsonArray, thread);
                            recycler_items.setAdapter(recyclerAdapterWalletItemsItem);
                        } else
                            findViewById(R.id.tv_empty_wallet).setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        findViewById(R.id.tv_empty_wallet).setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }
            }

        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.wallet));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//      setupUI();
        recycler_items = (RecyclerView) findViewById(R.id.recycler_wallet_items);
        mLayoutManager = new LinearLayoutManager(this);
        recycler_items.setLayoutManager(mLayoutManager);
//      recycler_offers.setLayoutManager(mLayoutManager);
        findViewById(R.id.layout_see_offers).setOnClickListener(this);
        findViewById(R.id.tv_empty_wallet).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_see_offers:
                startActivity(new Intent(this, DialogWalletOffers.class));
                break;
            case R.id.tv_empty_wallet:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_msg) + getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE).getString("referral_code", "0"));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_through)));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("00", "wallet activity exit");
        thread[0] = false;
    }

}
