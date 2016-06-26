package prashushi.farcon;

/**
 * Created by Dell User on 5/4/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//1 425 3 425 3
public class RecyclerAdapterCart extends Adapter<RecyclerAdapterCart.ViewHolder> {

    private Context mContext;
    JSONArray items;
    ArrayList<String> item_id, item_name, item_cost, item_qty, item_flag;
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    private DBHelper mydb ;
    TextView etQuantity, tvTotal;
    String _quantity, _qty0, _id, _cost;
    int _i;
    TextView cart_summary, cart_summary2;
    TextView _tvQ, _tvT;
    RelativeLayout summary_container;
    public RecyclerAdapterCart(Context context, JSONArray items, TextView tv, TextView tv2, RelativeLayout rl) {
        mContext = context;
        this.items=items;
        item_id=new ArrayList<>();
        item_name=new ArrayList<>();
        item_cost=new ArrayList<>();
        item_qty=new ArrayList<>();
        item_flag=new ArrayList<>();
        sPrefs=context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        editor=sPrefs.edit();
        mydb = new DBHelper(context);
        cart_summary=tv;
        cart_summary2=tv2;
        summary_container=rl;
        updateList(items);
    }

    private void updateList(JSONArray items) {
        try {
        for(int i=0;i<items.length();i++) {
            JSONObject item = null;
//            System.out.println(position);
            item = items.getJSONObject(i);
            item_id.add(i, item.optInt("item_id") + "");
            item_cost.add(i, item.optString("item_cost") + "");
            item_qty.add(i, item.optString("item_qty") + "");
            item_flag.add(i, item.optString("flags") + "");
            String name="";
            if(item.has("item_name"))
            name = item.optString("item_name")+"";
            else
                name = item.optString("name")+"";

            item_name.add(i, capitalize(name) + "");
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        int i=0;
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public ViewHolder(final View itemView) {
            super(itemView);
            etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
            tvTotal=(TextView) itemView.findViewById(R.id.tv_total);

/*            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do something
                    System.out.println("*3");
                    i=getAdapterPosition();
                    Intent intent=new Intent(mContext, ItemActivity.class);
                    intent.putExtra("item_id", item_id.get(i));
                    intent.putExtra("item_name", item_name.get(i));
                    intent.putExtra("item_cost", item_cost.get(i));
                    intent.putExtra("item_qty", item_qty.get(i));
                    mContext.startActivity(intent);

                }
            });
*/

            itemView.findViewById(R.id.bt_minus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
                    tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
                    int n=Integer.parseInt(etQuantity.getText().toString());
                    if(n>0) {
                        etQuantity.setText((n - 1) + "");
                        item_qty.set(i,(n-1) + "");
                        tvTotal.setText( "Rs. "+(n-1)*Double.valueOf(item_cost.get(i))) ;

                    }

                    updateCart(i, item_id.get(i), item_name.get(i), item_cost.get(i),(n-1)+"", etQuantity , tvTotal );
  //                  System.out.println("**"+item_name.get(i));

                }
            });
            itemView.findViewById(R.id.bt_plus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
                    tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
                    int n=Integer.parseInt(etQuantity.getText().toString());
                    if(n<999) {
                        etQuantity.setText((n + 1) + "");
                        item_qty.set(i,(n+1)+"");
                        tvTotal.setText( "Rs. "+(n+1)*Double.valueOf(item_cost.get(i))) ;
                    }
                    updateCart(i, item_id.get(i), item_name.get(i), item_cost.get(i),(n+1)+"", etQuantity  , tvTotal);
//                    System.out.println("***"+item_name.get(i));

                }
            });
            itemView.findViewById(R.id.bt_delete).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
                    tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
                    int n=Integer.parseInt(etQuantity.getText().toString());
                    tvTotal.setText( "Rs. 0.0") ;
                    item_qty.set(i,"0");
                    updateCart(i, item_id.get(i), item_name.get(i), item_cost.get(i),item_qty.get(i), etQuantity , tvTotal);
           //         System.out.println("****"+item_name.get(i));

                }
            });
        }
    }

    void updateCart(int i, final String id, String name, String cost, String qty, TextView tvQ, TextView tvT){
        _id=id;
        _i=i;
        _cost=cost;
        _quantity="";
        _tvQ=tvQ;
        _tvT=tvT;
        _qty0=qty;

        if(mydb.ifPresent(id)){
            _quantity=mydb.getQuantity(id);
            mydb.updateItem(id, cost,qty);
        }


        //interaction with summary
        System.out.println("Added to cart!");
        //removing


        double costd=0;
        String costj, qtyj;
        for(int j=0;j<item_id.size();j++){
            costj=item_cost.get(j);
            qtyj=item_qty.get(j);
            costd+=(Double.valueOf(costj)*Double.valueOf(qtyj));
        }
        //summary end
        sPrefs=mContext.getSharedPreferences(mContext.getString(R.string.S_PREFS), mContext.MODE_PRIVATE);
        String user_id=sPrefs.getString("id", "0");

        String url=mContext.getString(R.string.local_host)+mContext.getString(R.string.add_cart)+user_id+
                "/"+id+"/"+qty+"/"+(Double.valueOf(cost)*(Double.valueOf(qty)))+"/"+item_flag.get(_i);
        //to convert into float
        //same work in recyclershop when adding

        new BackgroundTask(url, new ArrayList<String>(), new ArrayList<String>(), new BackgroundTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                System.out.println(output);
                if(!output.contains("truexxx")){
                    //remove the recent added item
                    mydb.updateItem(_id, _cost,_quantity);
                    _tvQ.setText(_quantity);
                    item_qty.set(_i,_quantity);
                    _tvT.setText( "Rs. "+Double.valueOf(_quantity)*Double.valueOf(item_cost.get(_i))) ;
                    Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_LONG).show();
                }
                else {
                    //n+" Item \nTotal: Rs."+cost

                }
            }
        }).execute();

        if(Integer.parseInt(_qty0)==0){
            item_qty.remove(_i);
            item_id.remove(_i);
            item_name.remove(_i);
            item_cost.remove(_i);
            item_flag.remove(_i);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
                items.remove(_i);
            notifyItemRemoved(_i);
            notifyItemRangeChanged(_i, item_id.size());
            notifyDataSetChanged();
        }
        //__________________set cart empty
        JSONObject obj;
        double _cost=0;
        int j=0;
        for(;j<item_id.size();j++){
            _cost+=(Double.valueOf(item_cost.get(j))*Double.valueOf(item_qty.get(j)));
        }
        String st="";
        String st2;

        if(j>0)
        {st=j+" Item(s)";
            st2="Rs."+_cost;
        }
        else {st2="Cart Empty";
            summary_container.setClickable(false);}
        cart_summary.setText(st);
        cart_summary2.setText(st2);

}

    @Override
    public int getItemCount() {
        //      return cart.size();
        return items.length();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_cart, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(RecyclerAdapterCart.ViewHolder holder, int position) {
        int i=position;

        TextView name= (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(item_name.get(position));
        System.out.println("name:"+item_name.get(position));
        TextView item_costTv= (TextView) holder.itemView.findViewById(R.id.tv_price);
        item_costTv.setText(item_cost.get(position)+"/Kg");

        TextView item_qtyTv= (TextView) holder.itemView.findViewById(R.id.et_quantity);
        item_qtyTv.setText(item_qty.get(position));

        TextView totalTv= (TextView) holder.itemView.findViewById(R.id.tv_total);
        totalTv.setText("Rs. "+(item_qty.get(position))+"*"+(item_cost.get(position)));
        try{
            totalTv.setText( "Rs. "+(Double.valueOf(item_qty.get(position))*Double.valueOf(item_cost.get(position))) );
        }catch (Exception e){
            System.out.println("catch:"+item_qty.get(position)+","+item_cost.get(position));
            e.printStackTrace();
        }
   // System.out.println( "Rs. "+(Integer.parseInt(item_qty[position])*Integer.parseInt(item_cost[position])) );
        System.out.println("*****"+item_name.get(i));
    }

    private String capitalize(String name) {
        String st=name;
        if (name.length()==0)
            return name;
        if(name.charAt(0)>'Z'){
            st=(char)(name.charAt(0)-('a'-'A'))+name.substring(1);
        }
    return st;
    }
}