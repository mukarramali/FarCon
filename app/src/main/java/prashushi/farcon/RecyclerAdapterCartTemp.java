package prashushi.farcon;

/**
 * Created by Dell User on 7/1/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


//1 425 3 425 3
public class RecyclerAdapterCartTemp extends RecyclerView.Adapter<RecyclerAdapterCartTemp.ViewHolder> {

    private Context mContext;
    JSONArray items;
    ArrayList<String> item_id, item_name, item_cost_total, item_qty_pre, item_qty_pro;

    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    public RecyclerAdapterCartTemp(Context context, JSONArray items) {
        mContext = context;
        this.items=items;
        updateList(items);
    }

    private void updateList(JSONArray items) {
       item_id=new ArrayList<>();
        item_name=new ArrayList<>();
        item_cost_total=new ArrayList<>();
        item_qty_pre=new ArrayList<>();
        item_qty_pro=new ArrayList<>();
        try {
            for(int i=0;i<items.length();i++) {
                JSONObject item = null;
//            System.out.println(position);
                item = items.getJSONObject(i);
                item_id.add(i, item.optInt("item_id") + "");
                String name = item.optString("name")+"";
                item_name.add(i, capitalize(name) + "");
                item_cost_total.add(i, item.optDouble("offer_cost")/item.optInt("item_qty") + "");
                item_qty_pre.add(i, item.opt("item_qty") + "");
                item_qty_pro.add(i, item.opt("promo_qty") + "");
                //[{"item_id":1,"flags":0,"item_qty":2,"name":"gtf","offer_cost":0,"promo_qty":2},
                // {"item_id":1,"flags":0,"item_qty":1,"name":"gtf","offer_cost":8698,"promo_qty":1}]
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
        }
    }


    @Override
    public int getItemCount() {
        return items.length();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cart_card_temp, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(RecyclerAdapterCartTemp.ViewHolder holder, int position) {

        TextView name= (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(item_name.get(position));
        System.out.println("name:"+item_name.get(position));


        TextView item_qty1= (TextView) holder.itemView.findViewById(R.id.et_quantity_pre);
        item_qty1.setText(item_qty_pre.get(position)+"Kg");

        if(toInt(item_qty_pre.get(position))<toInt(item_qty_pro.get(position))) {
            TextView item_qty2 = (TextView) holder.itemView.findViewById(R.id.et_quantity_promo);
            item_qty2.setText(item_qty_pro.get(position) + "Kg");
        }
        TextView item_costTv= (TextView) holder.itemView.findViewById(R.id.tv_price);
        item_costTv.setText("Rs. "+format(Double.valueOf(item_cost_total.get(position)))+"/Kg");

        TextView item_totalTv= (TextView) holder.itemView.findViewById(R.id.tv_total);
        item_totalTv.setText("Rs. "+format(Double.valueOf(item_cost_total.get(position))*toInt(item_qty_pre.get(position))));

    }

    private String format(Double aDouble) {
    return String.format("%.1f", aDouble);
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

    int toInt(String s){
        if(s.compareTo("")==0)
            return 0;
        return Integer.parseInt(s);
    }
}