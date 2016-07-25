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
public class RecyclerAdapterOrders extends Adapter<RecyclerAdapterOrders.ViewHolder> {

    View _v;
    int _i;
    JSONArray orders;
    ArrayList<String> order_id, date_placed, date_shipped, date_delivered;
    ArrayList<Integer> cost, no_items, status;  //status:placed, shipped, delivered, cancelled
    ArrayList<JSONObject> orders_list;
    SharedPreferences sPrefs;
    private Context mContext;
    boolean[] thread;
    final Typeface roboto;

    public RecyclerAdapterOrders(Context context, JSONArray orders, boolean[] thread) {
        mContext = context;
        this.orders = orders;
        this.thread = thread;
        sPrefs = context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        updateList(orders);
        roboto = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_light.ttf");
    }

    void updateList(JSONArray orders) {

        orders_list = new ArrayList<>();
        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = null;
            System.out.println("***1");
            try {
                order = orders.getJSONObject(i);
                orders_list.add(order);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public int getItemCount() {
        //      return shop.size();
        return orders.length();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_orders, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterOrders.ViewHolder holder, int position) {
        int i = position;

        String date = "", id = "", amount_pay = "", items_count = "", order_status = "";
        int flag = 0;//0:not delivered, 1:processed, 2:delivered, 3:cancelled
        date = orders_list.get(i).optString("date_of_order");
        id = orders_list.get(i).optString("id");      //tracking_no still not used
        amount_pay = orders_list.get(i).optString("total_amt");
        items_count = orders_list.get(i).optString("item_count");
        order_status = orders_list.get(i).optString("order_status").toLowerCase();
        if (order_status.contains("cancel"))
            flag = 3;
        else if (order_status.contains("deliver"))
            flag = 2;


        TextView tv_date_placed = (TextView) holder.itemView.findViewById(R.id.tv_date);
        TextView tv_tracking_id = (TextView) holder.itemView.findViewById(R.id.tv_id);
        TextView tv_amount = (TextView) holder.itemView.findViewById(R.id.tv_amount);
        TextView tv_no_items = (TextView) holder.itemView.findViewById(R.id.tv_items);
        TextView tv_status = (TextView) holder.itemView.findViewById(R.id.tv_status);

        tv_date_placed.setText("Placed:" + date);
        tv_tracking_id.setText("Order ID:" + id);
        tv_amount.setText("Amount Payable:" + amount_pay);
        tv_no_items.setText(items_count + " Item(s)");
        tv_status.setText(capitalize(order_status));

/*        RelativeLayout ball1= (RelativeLayout) holder.itemView.findViewById(R.id.fab1);
        RelativeLayout ball2= (RelativeLayout) holder.itemView.findViewById(R.id.fab2);
        RelativeLayout ball3= (RelativeLayout) holder.itemView.findViewById(R.id.fab3);
        View rod1=holder.itemView.findViewById(R.id.rod1);
        View rod2=holder.itemView.findViewById(R.id.rod2);

        //0:not delivered, 1:processed, 2:delivered, 3:cancelled
        switch (flag){
            case 0:
                ball1.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_green));
                ball2.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_grey));
                ball3.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_grey));
                rod1.setBackgroundColor(mContext.getResources().getColor(R.color.green_dark));
                rod2.setBackgroundColor(mContext.getResources().getColor(R.color.greydark));
                break;
            case 1:
                break;
            case 2:
                ball1.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_green));
                ball2.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_green));
                ball3.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_green));
                rod1.setBackgroundColor(mContext.getResources().getColor(R.color.green_dark));
                rod2.setBackgroundColor(mContext.getResources().getColor(R.color.green_dark));

                break;
            case 3:
                ball1.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_red));
                ball2.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_grey));
                ball3.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.circle_grey));
                rod1.setBackgroundColor(mContext.getResources().getColor(R.color.greydark));
                rod2.setBackgroundColor(mContext.getResources().getColor(R.color.greydark));
                break;
        }
        */
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
                    intent.putExtra("order", orders.opt(i).toString());
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