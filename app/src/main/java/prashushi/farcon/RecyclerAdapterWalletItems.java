package prashushi.farcon;

/**
 * Created by Dell User on 7/11/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

//1 425 3 425 3
public class RecyclerAdapterWalletItems extends Adapter<RecyclerAdapterWalletItems.ViewHolder> {

    View _v;
    int _i;
    JSONArray items;
    ArrayList<String> item_referral_id, item_name, item_thumbnail, item_qty, item_count, item_content;
    ArrayList<Boolean> item_is_combo;
    SharedPreferences sPrefs;
    private Context mContext;
    boolean[] thread;
    final Typeface roboto;
    private LruCache<String, Bitmap> mMemoryCache;

    public RecyclerAdapterWalletItems(Context context, JSONArray items, boolean[] thread) {
        mContext = context;
        this.items = items;
        this.thread = thread;
        sPrefs = context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        updateList(items);
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

    void updateList(JSONArray items) {
        item_referral_id = new ArrayList<>();
        item_thumbnail = new ArrayList<>();
        item_name = new ArrayList<>();
        item_qty = new ArrayList<>();
        item_count = new ArrayList<>();
        item_qty = new ArrayList<>();
        item_is_combo = new ArrayList<>();
        item_content = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = null;
            System.out.println("***1");
            try {
                //[{"referral_id":1,"name":"aloo","item_qty":3,"item_combo":0,"item_count":2},
                // {"referral_id":1,"name":"combo","combo_qty":1,"item_combo":1,
                //     "items":[{"id":1,"item_id":1,"item_qty":22},{"id":2,"item_id":1,"item_qty":12}],"item_count":1}]
                item = items.getJSONObject(i);
                //check if count is 0
                int n = 0;
                n = item.optInt("item_count");
                if (n == 0)
                    continue;

                item_name.add(capitalize(item.optString("name")) + "");
                item_thumbnail.add(item.optString("thumbnail") + "");
                item_referral_id.add(item.optInt("referral_id") + "");
                item_count.add(item.optInt("item_count") + "");
                item_is_combo.add((item.optInt("item_combo") == 1 ? true : false));
                if (item_is_combo.get(i)) {
                    //get combo
                    System.out.println("Its combo");
                    item_qty.add(item.optString("combo_qty") + "");
                    item_content.add(fetchString(item.optJSONArray("items")));
                } else {
                    //get item
                    System.out.println("Its item");
                    item_qty.add(item.optString("item_qty") + "");
                    item_content.add("[]");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public int getItemCount() {
        //      return shop.size();
        return item_name.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_wallet_items, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterWalletItems.ViewHolder holder, int position) {
        int i = position;

        TextView name = (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(item_name.get(i));
        name.setTypeface(roboto);

        if (item_is_combo.get(i)) {
            //print content
            TextView combo_contentTv = (TextView) holder.itemView.findViewById(R.id.tv_content);
            combo_contentTv.setText(item_content.get(i));
            combo_contentTv.setTypeface(roboto);

        } else {
            //print size
            printSizePrice(holder.itemView, item_qty.get(i));
        }

        TextView count = (TextView) holder.itemView.findViewById(R.id.tv_count);
        count.setText(item_count.get(i));
        count.setTypeface(roboto);

        ImageView image = (ImageView) holder.itemView.findViewById(R.id.im_thumb);
        setImage(image, item_thumbnail.get(i));
    }

    private String fetchString(JSONArray array) {
        // "items":[{"id":1,"item_id":1,"item_qty":22},{"id":2,"item_id":1,"item_qty":12}],"item_count":1}]
        StringBuilder output = new StringBuilder();
        try {

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.optString("item_name");
                String qty = obj.optString("item_qty");
                output.append(capitalize(name) + " " + qty + "Kg\n");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output.toString();
    }


    private void printToast() {
        final Toast toast = Toast.makeText(mContext, "Added to Cart", Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 300);

    }

    private void printSizePrice(View item, String size1) {
        TextView item_sizeTv = (TextView) item.findViewById(R.id.tv_size);
        item_sizeTv.setText(getSizeTag(Double.valueOf(size1)));
        item_sizeTv.setTypeface(roboto);
    }

    private String getSizeTag(Double size_d) {
        String tag = "Kg";
        if (size_d < 1.0) {
            size_d *= 1000;
            tag = "grams";
        }
        return size_d + " " + tag;
    }

    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        int i = 0;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public ViewHolder(final View itemView) {
            super(itemView);
            System.out.println("***4");


            itemView.findViewById(R.id.bt_add).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = getAdapterPosition();
                    moveToCart(i, itemView);
                }
            });
        }
    }

    private void moveToCart(int i, View v) {
        _i = i;
        _v = v;
        String url = mContext.getString(R.string.local_host) + "referraladdcart";

        ArrayList<String> params = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        params.add("id");
        values.add(sPrefs.getString("id", "0"));
        params.add("referral_id");
        values.add(item_referral_id.get(i));
        params.add("item_count");
        values.add("1");
        params.add("item_combo");
        values.add(item_is_combo.get(i) ? "1" : "0");
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        new BackgroundTaskPost(mContext, url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                output = "truexxx";
                if (output.contains("truexxx") || output.contains("Truexxx")) {

                    TextView tv = (TextView) _v.findViewById(R.id.tv_count);
                    tv.setTypeface(roboto);
                    int qty = Integer.parseInt(tv.getText().toString());
                    if (qty == 1) {
                        //make it zero, and remove item from list
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                            items.remove(_i);
                        item_referral_id.remove(_i);
                        item_name.remove(_i);
                        item_thumbnail.remove(_i);
                        item_qty.remove(_i);
                        item_count.remove(_i);
                        item_content.remove(_i);
                        notifyItemRemoved(_i);
//                        notifyItemRangeChanged(_i, item_name.size());
//                        notifyDataSetChanged();

                    } else {
                        //decrease by one,
                        qty--;
                        tv.setText(qty + "");
                        item_qty.set(_i, qty + "");
                        System.out.println("Updated at " + _i + ":" + qty);
                    }
                    printToast();
                }
            }
        }).execute();
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void setImage(ImageView image, String s) {

        String url = mContext.getString(R.string.local_host_web) + s;
        DownloadImage downloadImage = new DownloadImage(image, thread, mMemoryCache, s, mContext);
        downloadImage.execute(url);
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

}