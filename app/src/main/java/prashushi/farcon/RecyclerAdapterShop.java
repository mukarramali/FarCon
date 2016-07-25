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
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//1 425 3 425 3
public class RecyclerAdapterShop extends Adapter<RecyclerAdapterShop.ViewHolder> {

    SharedPreferences sPrefs;
    //SharedPreferences.Editor editor;
    EditText etQuantity;
    String id, quantity;
    String _quantity, _qty0, _id, _cost;
    int _i;
    View clickedItem;
    private Context mContext;
    FragmentManager fragmentManager;
    private DBHelper mydb ;
    boolean[] thread;
    private LruCache<String, Bitmap> mMemoryCache;
    final Typeface roboto;

    ArrayList<ShopItem> items;

    public RecyclerAdapterShop(Context context, FragmentManager fragmentManager, ArrayList<ShopItem> items, boolean[] thread) {
        mContext = context;
        this.fragmentManager=fragmentManager;
        this.items=items;
        this.thread = thread;
        Log.v("xxx", "thread in recyclerShop:" + thread[0]);
        sPrefs=context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        //editor=sPrefs.edit();
        mydb = new DBHelper(context);
        initCache();
        roboto = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_light.ttf");
        downloadImages();
    }

    private void downloadImages() {
        for (int i = 0; i < items.size(); i++) {
            DownloadImage downloadImage = new DownloadImage(null, thread, mMemoryCache, items.get(i).thumbnail(), mContext);
            String url = mContext.getString(R.string.local_host_web) + items.get(i).thumbnail();
            downloadImage.execute(url);
        }
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

    private void makeVisible(View v, Boolean b) {

        v.findViewById(R.id.layout_count).setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        v.findViewById(R.id.bt_add).setVisibility(b ? View.INVISIBLE : View.VISIBLE);

    }

    void updateCart(int i, View itemView, int c){
        _i=i;


        EditText et= (EditText) itemView.findViewById(R.id.et_quantity);
        int qty= Integer.parseInt(et.getText().toString());
        qty+=c;
        items.get(i).buycount(qty);
        et.setText(qty+"");


        makeVisible(itemView, qty!=0);
        printToast(c);
        String id = items.get(i).id();
        _id=id;
        String cost = items.get(i).cost();
        _cost=cost;

        if(mydb.ifPresent(id)){
            quantity=mydb.getQuantity(id);
            _quantity=quantity;
            mydb.updateItem(id, String.format("%.0f", (Double.valueOf(items.get(i).cost()) * (Double.valueOf(qty)))), qty + "");
        }
        else {
            _quantity="0";
            mydb.insertItem(items.get(i).id(), items.get(i).name(), String.format("%.0f", (Double.valueOf(items.get(i).cost()) * (Double.valueOf(qty)))), qty + "");
        }

        //summary end
        sPrefs=mContext.getSharedPreferences(mContext.getString(R.string.S_PREFS), mContext.MODE_PRIVATE);
        //sPrefs=mcontext.getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        String user_id=sPrefs.getString("id", "0");
        String url=mContext.getString(R.string.local_host)+mContext.getString(R.string.add_cart);

        ArrayList<String> params=new ArrayList<>();
        ArrayList<String> values=new ArrayList<>();

        params.add("id");
        values.add(user_id);
        params.add("item_package_id");
        values.add(_id);
        params.add("cost");
        values.add(removeZero(Double.valueOf(cost) * Double.valueOf(qty) + ""));
//        values.add(String.format("%.1f",Double.valueOf(cost)*Double.valueOf(qty))+"");
        params.add("qty");
//        values.add(qty+"");
        values.add(c + "");
        params.add("item_combo");
        values.add("0");
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                System.out.println(output);
                if(!output.contains("truexxx")){
                    //remove the recent added item
                    mydb.updateItem(_id, _cost,_quantity);
                    new Utilities().checkIfLogged(output, mContext);
                }

            }
        }).execute();


    }

    private String removeZero(String package_qty) {
        double d = Double.valueOf(package_qty);
        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(d) + "";
    }

    private void printToast(int c) {
        final Toast toast = Toast.makeText(mContext, c == 0 || c == -1 ? "Removed" : "Added", Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 300);

    }




@Override
    public int getItemCount() {
        //      return shop.size();
    return items.size();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_shop_simple, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterShop.ViewHolder holder, int position) {
        int i=position;
        holder.setIsRecyclable(false);


        TextView name= (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(items.get(i).name());
        name.setTypeface(roboto);

        TextView hindi_name = (TextView) holder.itemView.findViewById(R.id.tv_hindi_name);
        hindi_name.setText("(" + items.get(i).hindi_name() + ")");
        hindi_name.setTypeface(roboto);


        printSizePrice(holder.itemView, items.get(i).cost(), items.get(i).pkg_qty());

        TextView item_offerTv = (TextView) holder.itemView.findViewById(R.id.tv_offer);
        item_offerTv.setTypeface(roboto);

        int prcnt = Integer.parseInt(items.get(i).percent_off());
        //flag: 0 nothing, 1:offer, 2:new price
        switch (items.get(i).flag_offer()) {
            case 1:
                holder.itemView.findViewById(R.id.layout_offer).setVisibility(View.VISIBLE);
                //item_offerTv.setText("Buy " + items.get(i).min_qty() + "Kg, Get " + items.get(i).percent_off() + "% Off");
                item_offerTv.setText(items.get(i).offer());
                break;
            case 2:
                holder.itemView.findViewById(R.id.layout_offer).setVisibility(View.INVISIBLE);
                TextView cost2 = (TextView) holder.itemView.findViewById(R.id.tv_price2);
                cost2.setTypeface(roboto);
                cost2.setText(items.get(i).offer() + mContext.getString(R.string.Rs));
                TextView cost1 = (TextView) holder.itemView.findViewById(R.id.tv_price);
                cost1.setPaintFlags(cost1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                break;
            default:
                holder.itemView.findViewById(R.id.layout_offer).setVisibility(View.INVISIBLE);
//                TextView cost = (TextView) holder.itemView.findViewById(R.id.tv_price);
                //              cost.setPaintFlags(0);

        }

        if (items.get(i).buycount() == 0)
            makeVisible(holder.itemView, false);
        else {
            makeVisible(holder.itemView, true);
            TextView etQ = (TextView) holder.itemView.findViewById(R.id.et_quantity);
            etQ.setText(items.get(i).buycount() + "");
        }
        ImageView image= (ImageView) holder.itemView.findViewById(R.id.im_thumb);
        try {
            setImage(image, items.get(i).thumbnail());
        } catch (Exception e) {
            image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.veg2));
            e.printStackTrace();
        }
    }


    public ShopItem removeItem(int position) {
        final ShopItem item = items.remove(position);
        notifyItemRemoved(position);
//        notifyDataSetChanged();
        return item;
    }

    public void addItem(int position, ShopItem model) {
        items.add(position, model);
//        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ShopItem item = items.remove(fromPosition);
        items.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
//        notifyDataSetChanged();
    }

    public void animateTo(ArrayList<ShopItem> newItems) {
        System.out.println("animate to:" + newItems.size());
        applyAndAnimateRemovals(newItems);
        applyAndAnimateAdditions(newItems);
        applyAndAnimateMovedItems(newItems);
        System.out.println("Remaining size:" + items.size());
        notifyDataSetChanged();
    }

    private void applyAndAnimateRemovals(ArrayList<ShopItem> newItems) {
        System.out.println("animateRemovals to:" + newItems.size());
        for (int i = items.size() - 1; i >= 0; i--) {
            final ShopItem model = items.get(i);
            if (!newItems.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<ShopItem> newItems) {
        System.out.println("animateAdditions to:" + newItems.size());
        for (int i = 0, count = newItems.size(); i < count; i++) {
            final ShopItem model = newItems.get(i);
            if (!items.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<ShopItem> newItems) {
        System.out.println("animateMove to:" + newItems.size());
        for (int toPosition = newItems.size() - 1; toPosition >= 0; toPosition--) {
            final ShopItem item = newItems.get(toPosition);
            final int fromPosition = items.indexOf(item);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void printSizePrice(View clickedItem, String price1, String size1) {
        try {
            double ds = Double.valueOf(size1);
            size1 = String.format("%.0f", ds) + "";
            double dp = Double.valueOf(price1);
            price1 = String.format("%.0f", dp) + "";


        } catch (Exception e) {

        }
        TextView item_costTv= (TextView) clickedItem.findViewById(R.id.tv_price);
        item_costTv.setText(price1+" "+mContext.getResources().getString(R.string.Rs));
        item_costTv.setTypeface(roboto);
        TextView item_sizeTv= (TextView) clickedItem.findViewById(R.id.tv_spinner);
        item_sizeTv.setText(getSizeTag(Double.valueOf(size1)));
        item_sizeTv.setTypeface(roboto);
    }

    private String getSizeTag(Double size_d) {
        String tag="Kg";
        if(size_d<1.0)
        {
            size_d*=1000;
            tag = "gms";
        }
        return size_d+" "+tag;
    }
    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        int i = 0;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public ViewHolder(final View itemView) {
            super(itemView);
            etQuantity = (EditText) itemView.findViewById(R.id.et_quantity);


            itemView.findViewById(R.id.spinner).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> size, price, id;
                    size = new ArrayList<>();
                    price = new ArrayList<>();
                    id = new ArrayList<>();
                    JSONArray packageArray = items.get(getAdapterPosition()).item_package();
                    for (int i = 0; i < packageArray.length(); i++) {
                        JSONObject temp = null;
                        try {
                            temp = packageArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        size.add(temp.optString("package_qty"));
                        id.add(temp.optInt("id") + "");
                        price.add(temp.optString("item_cost") + "");
                    }
                    DialogListPackage dialogListPackage = DialogListPackage.newInstance(id, size, price, getAdapterPosition(), items);
                    dialogListPackage.show(fragmentManager, null);
                    clickedItem = v;
                }
            });

            itemView.findViewById(R.id.bt_minus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = getAdapterPosition();
                    updateCart(i, itemView, -1);

                }
            });
            itemView.findViewById(R.id.bt_plus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = getAdapterPosition();
                    updateCart(i, itemView, 1);

                }
            });
            itemView.findViewById(R.id.bt_add).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = getAdapterPosition();
                    EditText et = (EditText) itemView.findViewById(R.id.et_quantity);
                    et.setText("0");
                    updateCart(i, itemView, 1);

                }
            });

            itemView.findViewById(R.id.mark).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = getAdapterPosition();
                    Intent intent = new Intent(mContext, DialogMarkPrice.class);
                    intent.putExtra("id", items.get(i).id());
                    intent.putExtra("cost", items.get(i).cost());
                    intent.putExtra("size", items.get(i).pkg_qty());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void setImage(ImageView image, String s) {
        Bitmap bitmap = getBitmapFromMemCache(s);
        if (bitmap != null) {
            System.out.println("Image set from cache!");
            image.setImageBitmap(bitmap);
        } else {
            String url = mContext.getString(R.string.local_host_web) + s;
            Log.v("xxx", "thread in recyclerShop2:" + thread[0]);
            DownloadImage downloadImage = new DownloadImage(image, thread, mMemoryCache, s, mContext);
            downloadImage.execute(url);
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


}