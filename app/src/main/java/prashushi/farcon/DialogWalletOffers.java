package prashushi.farcon;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Dell User on 7/11/2016.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class DialogWalletOffers extends Activity implements View.OnClickListener {

    RecyclerView recycler_offers;
    LinearLayoutManager mLayoutManager;
    SharedPreferences sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wallet_offer);
        recycler_offers = (RecyclerView) findViewById(R.id.recycler_wallet_offers);
        mLayoutManager = new LinearLayoutManager(this);
        recycler_offers.setLayoutManager(mLayoutManager);

        sPrefs = getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        String id = sPrefs.getString("id", "0");
        String url = getString(R.string.local_host) + "walletoption";
        ArrayList<String> params = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
//9808185808    8958050840
        params.add("id");
        values.add(id);
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        System.out.println("_token:" + sPrefs.getString("access_token", "0"));
        new BackgroundTaskPost(this, url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                System.out.println(output);
/*                output="[{\"id\":1,\"name\":\"combo\",\"image\":\"\",\"thumbnail\":\"\",\"referral_count\":2,\"items\":" +
                                   "[{\"id\":1,\"item_id\":1,\"item_qty\":22,\"item_name\":\"aloo\"}," +
                                    "{\"id\":2,\"item_id\":1,\"item_qty\":12,\"item_name\":\"aloo\"}]}]";
  */
                if (output.contains("error")) {
                    new Utilities().checkIfLogged(output, DialogWalletOffers.this);
                    finish();
                    return;
                } else if (output.contains("falsexxx")) {
                    Toast.makeText(DialogWalletOffers.this, getString(R.string.no_offers), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    findViewById(R.id.layout_card_offer).setVisibility(View.VISIBLE);
                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(output);
                        if (jsonArray.length() > 0) {
                            RecyclerAdapterWalletOffers recyclerAdapterWalletOffers = new RecyclerAdapterWalletOffers(DialogWalletOffers.this, jsonArray);
                            recycler_offers.setAdapter(recyclerAdapterWalletOffers);
                        } else {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).execute();


    }


    @Override
    public void onClick(View v) {

    }
}
