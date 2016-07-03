package prashushi.farcon;

/**
 * Created by Dell User on 5/4/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
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

    JSONArray items;
    ArrayList<String> item_id, item_name, item_cost, item_qty, item_flag, percent_off, min_qty, ref_flag;
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    TextView etQuantity, tvTotal, tvTotal2;
    String _quantity, _qty0, _id, _cost;
    int _i;
   TextView cart_summary_actual;
    TextView  cart_summary_final;
    TextView tv_cart_discount;
    TextView _tvQ, _tvT1, _tvT2;
    RelativeLayout summary_container;
    double min_amount;
    int discount_off;
    private Context mContext;
    private DBHelper mydb ;

    public RecyclerAdapterCart(Context context, double min_amount, int discount_off,JSONArray items, TextView tv, TextView tv2, TextView tv3,RelativeLayout rl) {
        mContext = context;
        this.items=items;
        this.min_amount=min_amount;
        this.discount_off=discount_off;
        sPrefs=context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        editor=sPrefs.edit();
        mydb = new DBHelper(context);
        cart_summary_actual=tv;
        cart_summary_final=tv2;
        tv_cart_discount=tv3;
        summary_container=rl;
        updateList(items);
    }

    private void updateList(JSONArray items) {
        item_id=new ArrayList<>();
        item_name=new ArrayList<>();
        item_cost=new ArrayList<>();
        item_qty=new ArrayList<>();
        item_flag=new ArrayList<>();
        percent_off=new ArrayList<>();
        min_qty=new ArrayList<>();
        ref_flag=new ArrayList<>();
        try {
        for(int i=0;i<items.length();i++) {
            JSONObject item = null;
//            System.out.println(position);
            item = items.getJSONObject(i);
            item_id.add(i, item.optInt("item_id") + "");
            item_cost.add(i, Double.valueOf(item.optString("item_cost"))/item.optInt("item_qty") + "");
            item_qty.add(i, item.optInt("item_qty") + "");
            item_flag.add(i, item.optInt("flags") + "");
            percent_off.add(i,item.optInt("percent_off")+"");
            String tmp;
            if(item.has("item_min_qty"))
                tmp = item.optInt("item_min_qty")+"";
            else
                tmp = item.optInt("combo_min_qty")+"";
            min_qty.add(i,tmp);
            ref_flag.add(i, item.optInt("item_flag", 0)+"");
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

    void updateCart(int i, final String id, String name, String cost, String qty, TextView tvQ, TextView tvT1, TextView tvT2){
        _id=id;
        _i=i;
        _cost=cost;
        _quantity="";
        _tvQ=tvQ;
        _tvT1=tvT1;
        _tvT2=tvT2;

        _qty0=qty;

        try {
            int mydbid = toInt(id);
            if(items.getJSONObject(i).has("name"))
                mydbid*=100;
            if (mydb.ifPresent(mydbid + "")) {
                _quantity = mydb.getQuantity(mydbid + "");
                mydb.updateItem(mydbid+"", cost, qty);
            }
        }catch (Exception e)
        {

        }


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



        String url=mContext.getString(R.string.local_host)+mContext.getString(R.string.add_cart);

        ArrayList<String> params=new ArrayList<>();
        ArrayList<String> values=new ArrayList<>();

        params.add("id");
        values.add(user_id);
        params.add("item_id");
        values.add(id);
        params.add("cost");
        values.add(String.format("%.1f",Double.valueOf(cost)*Double.valueOf(qty))+"");
        params.add("qty");
        values.add(qty);
        params.add("flag");
        values.add(item_flag.get(_i));
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        //to convert into float
        //same work in recyclershop when adding

        new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                System.out.println(output);
                if(!output.contains("truexxx")){
                    //remove the recent added item
                    try {
                        int mydbid = toInt(_id);
                        if(items.getJSONObject(_i).has("name"))
                            mydbid*=100;
                            mydb.updateItem(mydbid+"", _cost,_quantity);
                    }catch (Exception e)
                    {

                    }

                    _tvQ.setText(_quantity);
                    item_qty.set(_i,_quantity);
             setText(_tvT1, _tvT2, _i);
              new Utilities().checkIfLogged(output,mContext);
                }

            }
        }).execute();

        if(Integer.parseInt(_qty0)==0){
            item_qty.remove(_i);
            item_id.remove(_i);
            item_name.remove(_i);
            item_cost.remove(_i);
            item_flag.remove(_i);
            min_qty.remove(_i);
            percent_off.remove(_i);
            ref_flag.remove(_i);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
                items.remove(_i);
            notifyItemRemoved(_i);
            notifyItemRangeChanged(_i, item_id.size());
            notifyDataSetChanged();
        }
        //__________________set cart empty
        JSONObject obj;
        double _cost=0;
        double _off=0;
        int j=0;
        for(;j<item_id.size();j++){
            double temp=Double.valueOf(item_cost.get(j))*Double.valueOf(item_qty.get(j));
            if(toInt(percent_off.get(j))>0&&toInt(min_qty.get(j))<=toInt(item_qty.get(j)))
                _off+=(double)toInt(percent_off.get(j))*toInt(item_qty.get(j))*Double.valueOf(item_cost.get(j))/100;
            _cost+=temp;
        }
       String st1;
        String st2;

        double costnew=_cost-_off;
        double costfinal;
        if((int)costnew>min_amount&&discount_off>0) {
            costfinal = ((100 - discount_off) * costnew) / 100;
            tv_cart_discount.setText(discount_off+"% Discount");
        }
        else {
            costfinal = costnew;
            tv_cart_discount.setText("");
        }

        if(j>0)
        {
            st1="SubTotal: Rs."+String.format("%.1f",costnew);
            st2="Rs."+String.format("%.1f",costfinal);
        }
        else{
            st1="";
            st2="Cart Empty";
        }

        cart_summary_actual.setText(st1);
        cart_summary_final.setText(st2);
}

    @Override
    public int getItemCount() {
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

        TextView name= (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(item_name.get(position));
        System.out.println("name:"+item_name.get(position));


        TextView item_qtyTv= (TextView) holder.itemView.findViewById(R.id.et_quantity);
        item_qtyTv.setText(item_qty.get(position));

        TextView item_costTv= (TextView) holder.itemView.findViewById(R.id.tv_price);
        item_costTv.setText("Rs. "+(item_cost.get(position))+"/Kg");
        try{
            //String.format("%.1f", input)
            item_costTv.setText( "Rs. "+ item_cost.get(position)+"/Kg");
        }catch (Exception e){
            System.out.println("catch:"+item_qty.get(position)+","+item_cost.get(position));
            e.printStackTrace();
        }

        TextView item_totalTv= (TextView) holder.itemView.findViewById(R.id.tv_total);

        TextView item_totalTv2= (TextView) holder.itemView.findViewById(R.id.tv_total2);

        setText(item_totalTv, item_totalTv2, position);

        if(ref_flag.get(position).compareTo("0")!=0)
        {
            //invisible all buttons
            holder.itemView.findViewById(R.id.bt_plus).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.bt_minus).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.bt_delete).setVisibility(View.INVISIBLE);

        }
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

    String getCost(int per, String _cost, int qty){
        Double cost=Double.valueOf(_cost);
        String st=_cost;
       // int qty=Integer.parseInt(_qty);
        //int min=Integer.parseInt(_min);
        //int per=Integer.parseInt(_per);
           st= String.format("%.1f",((double)(100-per)*cost*qty)/100);
        return  st;
    }

    void setText(TextView tv1, TextView tv2, int i){
        int qty=Integer.parseInt(item_qty.get(i));
        int per=Integer.parseInt(percent_off.get(i));
        int min=Integer.parseInt(min_qty.get(i));
        tv1.setText( "Rs. "+String.format("%.1f",(qty)*Double.valueOf(item_cost.get(i)))) ;
        if(per>0&&qty>=min) {
         //   Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/italicslined.ttf");
           // tv1.setTypeface(tf);
            tv1.setPaintFlags(tv1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv2.setText("Sale: Rs. " + getCost(per, item_cost.get(i), qty));
        }
        else
        {
            //Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/myriad.ttf");
            //tv1.setTypeface(tf);
            tv1.setPaintFlags(0);
            tv2.setText("");

        }
    }

    int toInt(String s){
        if(s.compareTo("")==0)
            return 0;
        return Integer.parseInt(s);
    }

    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        int i=0;
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public ViewHolder(final View itemView) {
            super(itemView);
            etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
            tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
            tvTotal2=(TextView) itemView.findViewById(R.id.tv_total2);

            itemView.findViewById(R.id.bt_minus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
                    tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
                    tvTotal2=(TextView) itemView.findViewById(R.id.tv_total2);
                    int n=Integer.parseInt(etQuantity.getText().toString());
                    if(n>0) {
                        etQuantity.setText((n - 1) + "");
                        item_qty.set(i,(n-1) + "");
                        setText(tvTotal, tvTotal2, i);
                    }

                    updateCart(i, item_id.get(i), item_name.get(i), item_cost.get(i),(n-1)+"", etQuantity , tvTotal, tvTotal2 );
  //                  System.out.println("**"+item_name.get(i));

                }
            });
            itemView.findViewById(R.id.bt_plus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
                    tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
                    tvTotal2=(TextView) itemView.findViewById(R.id.tv_total2);
                    int n=Integer.parseInt(etQuantity.getText().toString());
                    if(n<999) {
                        etQuantity.setText((n + 1) + "");
                        item_qty.set(i,(n+1)+"");
                        setText(tvTotal, tvTotal2, i);
                    }
                    updateCart(i, item_id.get(i), item_name.get(i), item_cost.get(i),(n+1)+"", etQuantity  , tvTotal, tvTotal2);
//                    System.out.println("***"+item_name.get(i));

                }
            });
            itemView.findViewById(R.id.bt_delete).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
                    tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
                    tvTotal.setText( "Rs. 0.0") ;
                    //tvTotal2.setText( "Rs. 0.0") ;
                    item_qty.set(i,"0");
                    updateCart(i, item_id.get(i), item_name.get(i), item_cost.get(i),item_qty.get(i), etQuantity , tvTotal, tvTotal2);
           //         System.out.println("****"+item_name.get(i));

                }
            });
        }
    }
}