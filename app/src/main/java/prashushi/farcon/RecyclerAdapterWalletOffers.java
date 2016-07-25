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
public class RecyclerAdapterWalletOffers extends Adapter<RecyclerAdapterWalletOffers.ViewHolder> {

    View _v;
    int _i;
    JSONArray items;
    ArrayList<String> item_id, item_name, item_thumbnail, item_count, item_content;
    SharedPreferences sPrefs;
    private Context mContext;

    public RecyclerAdapterWalletOffers(Context context, JSONArray items) {
        mContext = context;
        this.items = items;
        System.out.println("***");

        sPrefs = context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        //editor=sPrefs.edit();
        updateList(items);
    }

    void updateList(JSONArray items) {
        item_id = new ArrayList<>();
        item_thumbnail = new ArrayList<>();
        item_name = new ArrayList<>();
        item_count = new ArrayList<>();
        item_content = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = null;
            System.out.println("***1");
            try {
//                [{"id":1,"name":"combo","image":"","thumbnail":"","referral_count":2,
//                          "items":[{"id":1,"item_id":1,"item_qty":22,"item_name":"aloo"},
//                                   {"id":2,"item_id":1,"item_qty":12,"item_name":"aloo"}]}]  
                item = items.getJSONObject(i);
                item_name.add(capitalize(item.optString("name")) + "");
                item_thumbnail.add(item.optString("thumbnail") + "");
                item_id.add(item.optInt("id") + "");
                item_count.add(item.optInt("referral_count") + "");
                item_content.add(fetchString(item.optJSONArray("items")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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


    @Override
    public int getItemCount() {
        //      return shop.size();
        return items.length();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_wallet_offers, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterWalletOffers.ViewHolder holder, int position) {
        int i = position;

        TextView name = (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(item_name.get(i));

        TextView combo_contentTv = (TextView) holder.itemView.findViewById(R.id.tv_content);
        combo_contentTv.setText(item_content.get(i));

        TextView count = (TextView) holder.itemView.findViewById(R.id.tv_count);
        count.setText(item_count.get(i));

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
                    useOffer(i, itemView);
                }
            });
        }
    }

    private void useOffer(int i, View v) {
        _i = i;
        _v = v;
        String url = mContext.getString(R.string.local_host) + "selectoption";

        ArrayList<String> params = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        params.add("id");
        values.add(sPrefs.getString("id", "0"));
        params.add("offer_id");
        values.add(item_id.get(i));
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                output = "truexxx";
                if (output.contains("truexxx") || output.contains("Truexxx")) {

                    Activity activity = (Activity) mContext;
                    mContext.startActivity(new Intent(mContext, WalletActivity.class));
                    activity.finish();
                    Toast.makeText(mContext, mContext.getString(R.string.offer_selected), Toast.LENGTH_LONG).show();
                } else {
                    //decrease by one,
                    Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_LONG).show();
                }
            }

        }).execute();
    }


    private void setImage(ImageView image, String s) {

        String url = mContext.getString(R.string.local_host_web) + s;
        DownloadImage downloadImage = new DownloadImage(image, new boolean[]{true});
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