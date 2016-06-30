package prashushi.farcon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class CartActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerview_cart;
    TextView  actualsummaryTv; //for actual cost
    TextView finalsummaryTv2 ;
    TextView discountTv;
    TextView tvBuy;
    DBHelper mydb;
    SharedPreferences sPrefs;
    String cartString;
    int count=0;
    RelativeLayout summary_container, promo_container;
    Boolean promo_flag=false;

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
        actualsummaryTv= (TextView) findViewById(R.id.tv_total_actual);
        finalsummaryTv2= (TextView) findViewById(R.id.tv_cart_summary2);
        discountTv= (TextView) findViewById(R.id.tv_discount);
        recyclerview_cart= (RecyclerView) findViewById(R.id.recycler_cart);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview_cart.setLayoutManager(mLayoutManager);
        tvBuy= (TextView) findViewById(R.id.tv_buy);
        summary_container= (RelativeLayout) findViewById(R.id.sumary_container);
        promo_container= (RelativeLayout) findViewById(R.id.promo_container);
        findViewById(R.id.tv_enter_promo).setOnClickListener(this);
        findViewById(R.id.bt_promo).setOnClickListener(this);
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
//        final RecyclerAdapterCart adapter=new RecyclerAdapterCart(this, jsonArray, summaryTv, summaryTv2, summary_container);
  //      recyclerview_cart.setAdapter(adapter);

        sPrefs=getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        String id=sPrefs.getString("id","0");
        String url=getString(R.string.local_host)+"cart";
        ArrayList<String> params=new ArrayList<>();
        ArrayList<String> values=new ArrayList<>();

        params.add("id");
        values.add(id);
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        System.out.println("_token:"+sPrefs.getString("access_token", "0"));
        new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                System.out.println(output);
                if(output.contains("error"))
                {
                    new Utilities().checkIfLogged(output,CartActivity.this);
                    return;
                }
                else{
                    JSONArray jsonArray= null;
                    JSONObject jsonObject=null;
                    JSONObject jsonDiscount=null;
                    try {

                        JSONArray temp=new JSONArray(output);
                        jsonObject=temp.getJSONObject(0);
                        jsonArray=jsonObject.optJSONArray("details");
                        temp=jsonObject.optJSONArray("details");
                        jsonDiscount=temp.getJSONObject(0);
                        int min_amount=jsonDiscount.optInt("min_amt");
                        int percent_off=jsonDiscount.optInt("percent_off");

                        cartString=jsonArray.toString();
                        System.out.println("Cart from Server:"+cartString);
                        final RecyclerAdapterCart adapter=new RecyclerAdapterCart(CartActivity.this,min_amount, percent_off, jsonArray, actualsummaryTv, finalsummaryTv2, discountTv,summary_container);
                        recyclerview_cart.setAdapter(adapter);
                        JSONObject obj;
                        double cost=0, costnew=0;
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
                        if((int)cost>min_amount&&percent_off>0)
                        {costnew=((100-percent_off)*cost)/100;
                         String st3=percent_off+"% Discount";
                        discountTv.setText(st3);
//                            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/italicslined.ttf");

                        }
                        else
                        costnew=cost;

                        if(j>0)
                        {
                            st="Rs."+String.format("%.1f",costnew);
                            st2="Rs."+String.format("%.1f",cost);
                        }
                        else{
                            st="";
                            st2="Cart Empty";
                        }
                        finalsummaryTv2.setText(st);
                        actualsummaryTv.setText(st2);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).execute();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_enter_promo:
                if(promo_flag) {
                    promo_flag=false;
                    promo_container.setVisibility(View.INVISIBLE);
                }else{
                    promo_flag=true;
                    promo_container.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.bt_promo:
                EditText etPromo= (EditText) findViewById(R.id.et_promo);
                String promo_code=etPromo.getText().toString();
                if(promo_code.length()<1)
                {
                    etPromo.setError("Invalid Promo code");
                    return;
                }
                String user_id=sPrefs.getString("id", "0");
                String url=getString(R.string.local_host)+"promo";
                //now call a function, get a value of promo code
                ArrayList<String> params=new ArrayList<>();
                ArrayList<String> values=new ArrayList<>();

                params.add("id");
                values.add(user_id);
                params.add("access_token");
                values.add(sPrefs.getString("access_token", "0"));
                params.add("code");
                values.add(promo_code);


                new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if(output.contains("falsexxx")) {
                            new Utilities().checkIfLogged(output, CartActivity.this);
                            return;
                        }
                        else {
                            JSONArray jsonArray = null;
                            JSONObject jsonObject = null;
                            JSONObject jsonCost = null;
                            try {

                                JSONArray temp = new JSONArray(output);
                                jsonObject = temp.getJSONObject(0);
                                jsonArray = jsonObject.optJSONArray("details");
                                jsonCost=temp.getJSONObject(1);
//                                double[] arr=(double[])jsonCost.opt("discount");
                                JSONArray arr=jsonCost.getJSONArray("discount");
                                if(arr.length()>2)
                                {
                                    System.out.println("Actual:"+arr.optDouble(0));
                                    System.out.println("Discounted:"+arr.optDouble(2));
                                    System.out.println("After Promo:"+arr.optDouble(1));

                                }
                                //[{"details":[{"item_id":1,"flags":0,"item_qty":2,"name":"gtf","offer_cost":0,"promo_qty":2},
                                // {"item_id":1,"flags":0,"item_qty":1,"name":"gtf","offer_cost":8698,"promo_qty":1}]},
                                // {"discount":[8698,7045.38,7828.2]}]
                            }catch (JSONException j){
                                j.printStackTrace();
                            }
                        }
                    }

                }).execute();
                break;

        }

    }

//    void  update(String st){
  //      summaryTv.setText(st);
    //}

}
