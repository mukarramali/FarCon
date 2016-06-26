package prashushi.farcon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dell User on 6/20/2016.
 */
public class CartActivity extends AppCompatActivity {
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerview_cart;
    TextView  summaryTv,summaryTv2 ;
    TextView tvBuy;
    DBHelper mydb;
    SharedPreferences sPrefs;
    String cartString;
    int count=0;
    RelativeLayout summary_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mydb=new DBHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.action_cart));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        summaryTv= (TextView) findViewById(R.id.tv_cart_summary);
        summaryTv2= (TextView) findViewById(R.id.tv_cart_summary2);

        recyclerview_cart= (RecyclerView) findViewById(R.id.recycler_cart);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview_cart.setLayoutManager(mLayoutManager);
        tvBuy= (TextView) findViewById(R.id.tv_buy);
        summary_container= (RelativeLayout) findViewById(R.id.sumary_container);
        summary_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count>0)
                startActivity(new Intent(CartActivity.this, AddressActivity.class));
            }
        });

        JSONArray jsonArray=mydb.getAllItems();
        cartString=jsonArray.toString();
        System.out.println("Cart from DB:"+cartString);
        final RecyclerAdapterCart adapter=new RecyclerAdapterCart(this, jsonArray, summaryTv, summaryTv2, summary_container);
        recyclerview_cart.setAdapter(adapter);

        sPrefs=getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        String id=sPrefs.getString("id","0");
        String url=getString(R.string.local_host)+"cart/"+id;
        new BackgroundTaskLoad(url, this,new ArrayList<String>(), new ArrayList<String>(), new BackgroundTaskLoad.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                if(output.contains("falsexxx"))
                {
                    Toast.makeText(CartActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
                    return;
                }  
                JSONArray jsonArray= null;
                try {
                    jsonArray = new JSONArray(output);
                    cartString=jsonArray.toString();
                    System.out.println("Cart from Server:"+cartString);

                    final RecyclerAdapterCart adapter=new RecyclerAdapterCart(CartActivity.this, jsonArray, summaryTv, summaryTv2, summary_container);
                    recyclerview_cart.setAdapter(adapter);

                    JSONObject obj;
                    double cost=0;
                    String costj, qtyj;
                    int j;
                    for(j=0;j<jsonArray.length();j++){
                        obj=jsonArray.getJSONObject(j);
                        costj=obj.optString("item_cost");
                        qtyj=obj.optString("item_qty");
                        cost+=(Double.valueOf(costj)*Double.valueOf(qtyj));
                    }
                    count=j;
                    String st="";
                    String st2="";

                    if(j>0)
                    {st=j+" Item(s)";
                        st2="Rs."+cost;
                    }
                    else st2="Cart Empty";
                    summaryTv.setText(st);
                    summaryTv2.setText(st2);


                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        }).execute();

    }

    void  update(String st){
        summaryTv.setText(st);
    }

}
