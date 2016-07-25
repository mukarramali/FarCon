package prashushi.farcon;

/**
 * Created by Dell User on 5/4/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

//1 425 3 425 3
public class RecyclerAdapterCart extends Adapter<RecyclerAdapterCart.ViewHolder> {

    JSONArray items;
    ArrayList<String> item_id, item_name, item_cost, item_qty, item_is_combo, item_thumbnail, percent_off, min_qty, ref_flag, referral_id, item_package_qty;
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
    boolean[] thread;
    final Typeface roboto;
    ;
    private LruCache<String, Bitmap> mMemoryCache;
    Integer count;
    double[] cost_new;


    public RecyclerAdapterCart(Context context, double min_amount, int discount_off, JSONArray items, TextView tv, TextView tv2, TextView tv3, RelativeLayout rl, boolean[] thread, Integer count, double[] cost_new) {
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
        this.thread = thread;
        updateList(items);
        this.count = count;
        this.cost_new = cost_new;
        roboto = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_light.ttf");
        initCache();
    }

    void initCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }


    private void updateList(JSONArray items) {
        item_id=new ArrayList<>();
        item_name=new ArrayList<>();
        item_cost=new ArrayList<>();
        item_qty=new ArrayList<>();
        item_is_combo = new ArrayList<>();
        percent_off=new ArrayList<>();
        min_qty=new ArrayList<>();
        ref_flag=new ArrayList<>();
        referral_id = new ArrayList<>();
        item_package_qty = new ArrayList<>();
        item_thumbnail = new ArrayList<>();
        try {
            //id for item is "id"
            //id for combo is "item_id"
        for(int i=0;i<items.length();i++) {
            JSONObject item = null;
            item = items.getJSONObject(i);
            item_id.add(i, item.optInt("item_package_id") + "");
            item_cost.add(i, Double.valueOf(item.optString("item_cost"))/item.optInt("item_qty") + "");
            //Double.valueOf(item.optString("item_cost"))/item.optInt("item_qty")
            item_qty.add(i, item.optInt("item_qty") + "");
            item_is_combo.add(i, item.optInt("item_combo") + "");
            percent_off.add(i,item.optInt("percent_off")+"");
            item_thumbnail.add(i, item.optString("thumbnail") + "");

            String tmp;
            if(item.has("item_min_qty"))
                tmp = item.optInt("item_min_qty")+"";
            else
                tmp = item.optInt("combo_min_qty")+"";
            min_qty.add(i,tmp);
            ref_flag.add(i, item.optInt("item_flag", 0)+"");
            referral_id.add(i, item.optInt("referral_id", 0) + "");
            String name="";
            if (item.has("item_name")) {
                name = item.optString("item_name") + "";
                item_package_qty.add(i, item.optString("package_qty"));
            } else {
                name = item.optString("name") + "";
                item_package_qty.add(i, item.optString("package_qty", "1"));

            }
            item_name.add(i, capitalize(name) + "");
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void updateCart(int i, final String id, String name, String cost, String qty, TextView tvQ, TextView tvT1, TextView tvT2, int c) {
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
        params.add("item_package_id");
        values.add(id);
        params.add("cost");
        values.add(String.format("%.1f",Double.valueOf(cost)*Double.valueOf(qty))+"");
        params.add("qty");
        values.add(c + "");
        //values.add(qty);
        params.add("item_combo");
        values.add(item_is_combo.get(_i));
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
                    new Utilities().checkIfLogged(output, mContext);
                }

            }
        }).execute();

        if(Integer.parseInt(_qty0)==0){
            removeItemAt(_i);
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

        cost_new[0] = _cost - _off;
        double costfinal;
        if (cost_new[0] >= min_amount && discount_off > 0) {
            costfinal = ((100 - discount_off) * cost_new[0]) / 100;
            tv_cart_discount.setText(discount_off+"% Discount");
        }
        else {
            costfinal = cost_new[0];
            tv_cart_discount.setText("");
        }

        if(j>0)
        {
            st1 = mContext.getString(R.string.Rs) + String.format("%.0f", (double) cost_new[0]);
            st2 = mContext.getString(R.string.Rs) + String.format("%.0f", costfinal);
        }
        else{
            st1="";
            st2="Cart Empty";

        }
        count = item_id.size();
        cost_new[0] = costfinal;
        System.out.println("cost_new updated:" + cost_new[0]);
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
        name.setTypeface(roboto);

        TextView item_qtyTv= (TextView) holder.itemView.findViewById(R.id.et_quantity);
        item_qtyTv.setText(item_qty.get(position) + (ref_flag.get(position).compareTo("1") != 0 ? "" : item_is_combo.get(position).compareTo("1") == 0 ? "Item(s)" : "Kg(s)"));
        item_qtyTv.setTypeface(roboto);

        TextView item_costTv= (TextView) holder.itemView.findViewById(R.id.tv_price);
        item_costTv.setTypeface(roboto);
        try{
//            item_costTv.setText( "Rs. "+ String.format("%.0f", item_cost.get(position))+getSizeTag(Double.valueOf(item_package_qty.get(position)), item_is_combo.get(position)));
            item_costTv.setText(getSizeTag(Double.valueOf(item_package_qty.get(position)), item_is_combo.get(position)));
        }catch (Exception e){
            System.out.println("catch:"+item_qty.get(position)+","+item_cost.get(position));
            e.printStackTrace();
        }

        TextView item_totalTv= (TextView) holder.itemView.findViewById(R.id.tv_total);
        item_totalTv.setTypeface(roboto);

        TextView item_totalTv2= (TextView) holder.itemView.findViewById(R.id.tv_total2);
        item_totalTv2.setTypeface(roboto);

        setText(item_totalTv, item_totalTv2, position);

        if(ref_flag.get(position).compareTo("0")!=0)
        {
            //invisible all buttons
            holder.itemView.findViewById(R.id.bt_plus).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.bt_minus).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.bt_delete).setVisibility(View.INVISIBLE);
            //visible wallet option
            item_costTv.setVisibility(View.INVISIBLE);
            item_totalTv.setVisibility(View.INVISIBLE);
            item_totalTv2.setVisibility(View.INVISIBLE);

            holder.itemView.findViewById(R.id.bt_move_to_wallet).setVisibility(View.VISIBLE);

        } else {
            holder.itemView.findViewById(R.id.bt_plus).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.bt_minus).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.bt_delete).setVisibility(View.VISIBLE);
            //visible wallet option
            item_costTv.setVisibility(View.VISIBLE);
            item_totalTv.setVisibility(View.VISIBLE);
            item_totalTv2.setVisibility(View.VISIBLE);

            holder.itemView.findViewById(R.id.bt_move_to_wallet).setVisibility(View.INVISIBLE);

        }
        ImageView image = (ImageView) holder.itemView.findViewById(R.id.im_thumb);
        setImage(image, item_thumbnail.get(position));

    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void setImage(ImageView image, String s) {


        Bitmap bitmap = getBitmapFromMemCache(s);
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        } else {
            String url = mContext.getString(R.string.local_host_web) + s.replace(" ", "");
            DownloadImage downloadImage = new DownloadImage(image, thread, mMemoryCache, s, mContext);
            downloadImage.execute(url);
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
            tvTotal2=(TextView) itemView.findViewById(R.id.tv_total2);

            itemView.findViewById(R.id.bt_minus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
                    etQuantity.setTypeface(roboto);
                    tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
                    tvTotal.setTypeface(roboto);
                    tvTotal2=(TextView) itemView.findViewById(R.id.tv_total2);
                    tvTotal2.setTypeface(roboto);
                    int n=Integer.parseInt(etQuantity.getText().toString());
                    if(n>0) {
                        etQuantity.setText((n - 1) + "");
                        item_qty.set(i,(n-1) + "");
                        setText(tvTotal, tvTotal2, i);
                    }
                    int c = -1;
                    if (n == 1)
                        c = 0;
                    updateCart(i, item_id.get(i), item_name.get(i), item_cost.get(i), (n - 1) + "", etQuantity, tvTotal, tvTotal2, c);
  //                  System.out.println("**"+item_name.get(i));

                }
            });
            itemView.findViewById(R.id.bt_plus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
                    etQuantity.setTypeface(roboto);
                    tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
                    tvTotal.setTypeface(roboto);
                    tvTotal2=(TextView) itemView.findViewById(R.id.tv_total2);
                    tvTotal2.setTypeface(roboto);
                    int n=Integer.parseInt(etQuantity.getText().toString());
                    if(n<999) {
                        etQuantity.setText((n + 1) + "");
                        item_qty.set(i,(n+1)+"");
                        setText(tvTotal, tvTotal2, i);
                    }
                    updateCart(i, item_id.get(i), item_name.get(i), item_cost.get(i), (n + 1) + "", etQuantity, tvTotal, tvTotal2, 1);
//                    System.out.println("***"+item_name.get(i));

                }
            });
            itemView.findViewById(R.id.bt_delete).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(TextView) itemView.findViewById(R.id.et_quantity);
                    etQuantity.setTypeface(roboto);
                    tvTotal=(TextView) itemView.findViewById(R.id.tv_total);
                    tvTotal.setTypeface(roboto);
                    tvTotal2 = (TextView) itemView.findViewById(R.id.tv_total2);
                    tvTotal2.setTypeface(roboto);
                    //tvTotal2.setText( "Rs. 0.0") ;
                    item_qty.set(i,"0");
                    updateCart(i, item_id.get(i), item_name.get(i), item_cost.get(i), item_qty.get(i), etQuantity, tvTotal, tvTotal2, 0);
           //         System.out.println("****"+item_name.get(i));

                }
            });
            itemView.findViewById(R.id.bt_move_to_wallet).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = getAdapterPosition();
                    moveToWallet(itemView, i);
                }
            });
        }
    }

    private void moveToWallet(View itemView, int i) {
        _i = i;
        sPrefs = mContext.getSharedPreferences(mContext.getString(R.string.S_PREFS), mContext.MODE_PRIVATE);
        String id = sPrefs.getString("id", "0");
        String url = mContext.getString(R.string.local_host) + "referraldeletecart";
        ArrayList<String> params = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
//9808185808    8958050840
        params.add("id");
        values.add(id);
        params.add("referral_id");
        values.add(referral_id.get(i));
        params.add("item_combo");
        values.add(item_is_combo.get(i));
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        System.out.println("_token here:" + sPrefs.getString("access_token", "0"));
        new BackgroundTaskPost(mContext, url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {

//                System.out.println("****");
                System.out.println(output);
                if (output.contains("truexxx")) {

                    Toast.makeText(mContext, "Item moved to Wallet", Toast.LENGTH_LONG).show();
                    removeItemAt(_i);
                    //notifyItemRangeChanged(_i, item_name.size());
                    notifyDataSetChanged();
                } else
                    Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }
        }).execute();
    }

    private String getSizeTag(Double size_d, String flag) {
        String tag = "";
        String size = String.format("%.0f", size_d);
        //   if(flag.compareTo("1")==0)
        //     tag+="Item";
        //  else
        if (size_d < 1 && size_d < 1.0) {
            size_d *= 1000;
            size = String.format("%.0f", size_d);
            tag += size + "grams";
        } else
            tag += size + "Kg";
        return tag;
    }

    private String capitalize(String name) {
        String st = name;
        if (name.length() == 0)
            return name;
        if (name.charAt(0) > 'Z') {
            st = (char) (name.charAt(0) - ('a' - 'A')) + name.substring(1);
        }
        return st;
    }

    String getCost(int per, String _cost, int qty) {
        Double cost = Double.valueOf(_cost);
        String st = _cost;
        // int qty=Integer.parseInt(_qty);
        //int min=Integer.parseInt(_min);
        //int per=Integer.parseInt(_per);
        st = String.format("%.0f", ((double) (100 - per) * cost * qty) / 100);
        return st;
    }

    void setText(TextView tv1, TextView tv2, int i) {
        int qty = Integer.parseInt(item_qty.get(i));
        int per = Integer.parseInt(percent_off.get(i));
        int min = Integer.parseInt(min_qty.get(i));
        tv1.setText(mContext.getString(R.string.Rs) + String.format("%.0f", (qty) * Double.valueOf(item_cost.get(i))));
        if (per > 0 && qty >= min) {
            //   Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/italicslined.ttf");
            // tv1.setTypeface(tf);
            tv1.setPaintFlags(tv1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv2.setText(mContext.getString(R.string.Rs) + getCost(per, item_cost.get(i), qty));
        } else {
            //Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/myriad.ttf");
            //tv1.setTypeface(tf);
            tv1.setPaintFlags(0);
            tv2.setText("");

        }
    }

    int toInt(String s) {
        if (s.compareTo("") == 0)
            return 0;
        return Integer.parseInt(s);
    }

    void removeItemAt(int i) {
        //    item_id, item_name, item_cost, item_qty, item_is_combo, percent_off, min_qty, ref_flag, referral_id

        item_qty.remove(_i);
        item_id.remove(_i);
        item_name.remove(_i);
        item_cost.remove(_i);
        item_is_combo.remove(_i);
        item_thumbnail.remove(_i);
        min_qty.remove(_i);
        percent_off.remove(_i);
        ref_flag.remove(_i);
        referral_id.remove(_i);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            items.remove(_i);

//        removeItemAt(_i);
        notifyItemRemoved(_i);
//        notifyItemRangeChanged(_i, item_id.size());
        notifyDataSetChanged();

    }
}