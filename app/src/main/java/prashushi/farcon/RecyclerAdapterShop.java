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
public class RecyclerAdapterShop extends Adapter<RecyclerAdapterShop.ViewHolder> {

    private Context mContext;
    JSONArray items;
    String[] item_id, item_name, item_cost, item_thumbnail;
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    private DBHelper mydb ;
    EditText etQuantity;
    String id, quantity;
    public RecyclerAdapterShop(Context context, JSONArray items) {
        mContext = context;
        this.items=items;
        System.out.println("***");
        item_id=new String[items.length()];
        item_thumbnail=new String[items.length()];
        item_name=new String[items.length()];
        item_cost=new String[items.length()];
        sPrefs=context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        editor=sPrefs.edit();
        mydb = new DBHelper(context);
    }
    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        int i=0;
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public ViewHolder(final View itemView) {
            super(itemView);
            System.out.println("***4");
            etQuantity=(EditText) itemView.findViewById(R.id.et_quantity);
/*
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do something
                    System.out.println("*3");
                    i=getAdapterPosition();
                    Intent intent=new Intent(mContext, ItemActivity.class);
                    intent.putExtra("item_id", item_id[i]);
                    intent.putExtra("item_name", item_name[i]);
                    intent.putExtra("item_cost", item_cost[i]);
                    mContext.startActivity(intent);

                }
            });

  */
            itemView.findViewById(R.id.bt_add).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    id=item_id[i];
                    etQuantity=(EditText) itemView.findViewById(R.id.et_quantity);
                    String qty=etQuantity.getText().toString();
                    if(qty.compareTo("0")==0)
                    {//Toast.makeText(mContext, "Removed", ).show();
                        final Toast toast = Toast.makeText(mContext, "Removed", Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 300);
                    }
                    else
                    {//Toast.makeText(mContext, "Removed", ).show();
                        final Toast toast = Toast.makeText(mContext, "Added", Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 300);
                    }
                    System.out.println("Quantity:"+qty);
                    quantity="";
                    if(mydb.ifPresent(id)){
                        quantity=mydb.getQuantity(id);
                        mydb.updateItem(id, item_cost[i],qty);
                    }
                    else
                        mydb.insertItem(item_id[i],item_name[i],item_cost[i], qty);

                    sPrefs=mContext.getSharedPreferences(mContext.getString(R.string.S_PREFS), mContext.MODE_PRIVATE);
                    String user_id=sPrefs.getString("id", "0");

                    String url=mContext.getString(R.string.local_host)+mContext.getString(R.string.add_cart)+user_id+
                            "/"+item_id[i]+"/"+etQuantity.getText().toString()+"/"
                            +(Double.valueOf(item_cost[i])*(Double.valueOf(qty)))+"/0";

                    new BackgroundTask(url, new ArrayList<String>(), new ArrayList<String>(), new BackgroundTask.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            System.out.println(output);
                            if(!output.contains("truexxx")){
                                //remove the recent added item
                                mydb.updateItem(id, item_cost[i],quantity);
                                Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_LONG).show();
                            }
                            else
                                System.out.println("Added to cart!");
                        }
                    }).execute();

                }
            });

            itemView.findViewById(R.id.bt_minus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(EditText) itemView.findViewById(R.id.et_quantity);
                    int n=Integer.parseInt(etQuantity.getText().toString());
                    if(n>0)
                    etQuantity.setText((n-1)+"");

                }
            });
            itemView.findViewById(R.id.bt_plus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    etQuantity=(EditText) itemView.findViewById(R.id.et_quantity);
                    int n=Integer.parseInt(etQuantity.getText().toString());
                    if(n<999)
                    etQuantity.setText((n+1)+"");

                }
            });
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
        System.out.println("***3");

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_shop_simple, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterShop.ViewHolder holder, int position) {
        int i=position;
        JSONObject item=null;
        System.out.println("***1");
        try {
            System.out.println(position);
            item=items.getJSONObject(position);
            item_id[i]=item.optInt("item_id")+"";
            item_name[i]=capitalize(item.optString("item_name"))+"";
            item_cost[i]=item.optString("item_cost")+"";
            item_thumbnail[i]=item.optString("thumbnail")+"";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(position);

        System.out.println("***2");

        TextView name= (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(item_name[position]);


        TextView item_costTv= (TextView) holder.itemView.findViewById(R.id.tv_price);
        item_costTv.setText(item_cost[position]+"/Kg");
        CircularImageView image= (CircularImageView) holder.itemView.findViewById(R.id.im_thumb);
        setImage(image, item_thumbnail[position]);
    }

    private void setImage(CircularImageView image, String s) {

        String url=mContext.getString(R.string.local_host_web)+s;
        DownloadImage downloadImage=new DownloadImage(image);
        downloadImage.execute(url);
    }
    class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView im;

        public DownloadImage(CircularImageView pstr) {
            im = pstr;
        }


        @Override
        protected void onPreExecute() {
            im.setImageResource(R.drawable.veg2);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = null;
            try {
                InputStream fin = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(fin);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if(bitmap!=null)
                im.setImageBitmap(bitmap);

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