package prashushi.farcon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Dell User on 6/30/2016.
 */
public class TempCartActivity extends Activity implements View.OnClickListener {
    RecyclerView recyclerview_cart;
    LinearLayoutManager mLayoutManager;
    String promo_code;
    String items, actual_cost, discount_cost, after_promo_cost;
    TextView tv_actual, tv_discount, tv_promo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_temp);
        recyclerview_cart= (RecyclerView) findViewById(R.id.recycler_cart);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview_cart.setLayoutManager(mLayoutManager);

        tv_actual= (TextView) findViewById(R.id.tv_cost_actual);
        tv_discount= (TextView) findViewById(R.id.tv_cost_discount);
        tv_promo= (TextView) findViewById(R.id.tv_cost_promo);
        findViewById(R.id.sumary_container).setOnClickListener(this);
        try{
            Bundle bundle=getIntent().getExtras();
            items=bundle.getString("items");
            actual_cost=bundle.getDouble("actual_cost")+"";
            discount_cost=bundle.getDouble("discount_cost")+"";
            after_promo_cost=bundle.getDouble("after_promo_cost")+"";
            promo_code=bundle.getString("code");
            tv_actual.setText(getString(R.string.Rs) + actual_cost);
            tv_discount.setText(getString(R.string.Rs) + discount_cost);
            tv_promo.setText(getString(R.string.Rs) + after_promo_cost);

        }catch(Exception e){
            e.printStackTrace();
            onBackPressed();
        }
        try {
            JSONArray jsonArray=new JSONArray(items);
            //[{"item_id":1,"flags":0,"item_qty":2,"name":"gtf","offer_cost":0,"promo_qty":2},
            // {"item_id":1,"flags":0,"item_qty":1,"name":"gtf","offer_cost":8698,"promo_qty":1}]
            RecyclerAdapterCartTemp recyclerAdapterCartTemp=new RecyclerAdapterCartTemp(this, jsonArray);
            recyclerview_cart.setAdapter(recyclerAdapterCartTemp);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sumary_container:
                Intent intent=new Intent(TempCartActivity.this, AddressActivity.class);
                intent.putExtra("code", promo_code);
                intent.putExtra("cost", tv_promo.getText().toString());
                startActivity(intent);
                break;
        }
    }
}
