package prashushi.farcon;

/**
 * Created by Dell User on 7/11/2016.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

//1 425 3 425 3
public class RecyclerAdapterOrderItems extends Adapter<RecyclerAdapterOrderItems.ViewHolder> {

    View _v;
    int _i;
    JSONArray items;
    ArrayList<JSONObject> items_list;
    SharedPreferences sPrefs;
    private Context mContext;
    boolean[] thread;
    final Typeface roboto;
    private LruCache<String, Bitmap> mMemoryCache;

    public RecyclerAdapterOrderItems(Context context, JSONArray items, boolean[] thread) {
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

        items_list = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject order = null;
            System.out.println("***1");
            try {
                order = items.getJSONObject(i);
                items_list.add(order);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public int getItemCount() {
        //      return shop.size();
        return items.length();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_order_items, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterOrderItems.ViewHolder holder, int position) {
        int i = position;
/*
{
    "id": 1,
    "customer_id": 1,
    "order_id": 1,
    "item_qty": 55,
    "item_cost": 1456,
    "status": 0,
    "item_id": 1,
    "referral_id": 0,
    "item_combo": 0,
    "item_flag": 0
  }
 */
        String item_id = "", item_qty = "", item_cost = "", item_name = "", item_combo = "", item_size = "", item_thumbnail;
        item_id = items_list.get(i).optString("item_id");
        item_qty = items_list.get(i).optString("item_qty");
        item_cost = items_list.get(i).optString("item_cost");
        if (items_list.get(i).has("item_name"))
            item_name = items_list.get(i).optString("item_name");
        else
            item_name = items_list.get(i).optString("name");

        item_combo = items_list.get(i).optString("item_combo");
        item_size = items_list.get(i).optString("package_qty"); //need to ask
        item_thumbnail = items_list.get(i).optString("thumbnail");


        TextView tv_name = (TextView) holder.itemView.findViewById(R.id.tv_name);
        TextView tv_cost = (TextView) holder.itemView.findViewById(R.id.tv_cost);
        TextView tv_size = (TextView) holder.itemView.findViewById(R.id.tv_size);
        TextView tv_count = (TextView) holder.itemView.findViewById(R.id.tv_count);

        tv_name.setText(capitalize(item_name));
        tv_cost.setText(item_cost + mContext.getString(R.string.Rs));
        tv_size.setText(item_size + "Kg");
        tv_count.setText(item_qty + " Pack(s)");


        ImageView image = (ImageView) holder.itemView.findViewById(R.id.im_thumb);
        setImage(image, item_thumbnail);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void setImage(ImageView image, String s) {


        Bitmap bitmap = getBitmapFromMemCache(s);
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        } else {
            String url = mContext.getString(R.string.local_host_web) + s;
            DownloadImage downloadImage = new DownloadImage(image, thread, mMemoryCache, s, mContext);
            downloadImage.execute(url);
        }
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

    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        int i = 0;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public ViewHolder(final View itemView) {
            super(itemView);
            //all child view clicks handle here
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = getAdapterPosition();
                    Activity activity = (Activity) mContext;
                    Intent intent = new Intent(activity, OrderActivity.class);
                    intent.putExtra("order", items.opt(i).toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(activity, itemView, "header");
                        activity.startActivity(intent, options.toBundle());
                        v.setTransitionName("header");
                        Slide slide = new Slide();
                        slide.setDuration(1000);
                        activity.getWindow().setSharedElementEnterTransition(slide);
                        activity.getWindow().setSharedElementExitTransition(slide);

                    } else {
                        activity.startActivity(intent);
                    }
                    activity.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                }
            });
        }
    }


}