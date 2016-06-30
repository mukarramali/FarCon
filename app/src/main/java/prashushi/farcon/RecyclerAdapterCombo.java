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
public class RecyclerAdapterCombo extends Adapter<RecyclerAdapterCombo.ViewHolder> {

    private Context mContext;
    JSONArray combos;
    String[] combo_id, combo_name, combo_cost, combo_content, combo_thumbnail, percent_off, min_qty;
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    private DBHelper mydb ;
    EditText etQuantity;
    String id, quantity;
    String _quantity, _qty0, _id, _cost;
    int _i;

    public RecyclerAdapterCombo(Context context, JSONArray combos) {
        mContext = context;
        System.out.println("###");
        this.combos=combos;
        combo_id=new String[combos.length()];
        combo_name=new String[combos.length()];
        combo_cost=new String[combos.length()];
        combo_content=new String[combos.length()];
        combo_thumbnail=new String[combos.length()];
        percent_off=new String[combos.length()];
        min_qty=new String[combos.length()];
        sPrefs=context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        editor=sPrefs.edit();
        mydb = new DBHelper(context);
    }
    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        int i=0;
        public ViewHolder(final View itemView) {
            super(itemView);
            System.out.println("###2");

            etQuantity=(EditText) itemView.findViewById(R.id.et_quantity);
/*
            comboView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do something
                    System.out.println("*3");
                    i=getAdapterPosition();
                    Intent intent=new Intent(mContext, ComboActivity.class);
                    intent.putExtra("combo_id", combo_id[i]);
                    intent.putExtra("combo_name", combo_name[i]);
                    intent.putExtra("combo_cost", combo_cost[i]);
                    mContext.startActivity(intent);

                }
            });

  */
            itemView.findViewById(R.id.bt_minus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    updateCart(i, itemView, -1);

                }
            });
            itemView.findViewById(R.id.bt_plus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    updateCart(i, itemView, +1);

                }
            });
        }
    }

    private void makeVisible(View v, Boolean b) {

        v.findViewById(R.id.bt_minus).setVisibility(b==true?View.VISIBLE:View.INVISIBLE);
        v.findViewById(R.id.et_quantity).setVisibility(b==true?View.VISIBLE:View.INVISIBLE);


    }

    void updateCart(int i, View itemView, int c){
        _i=i;


        EditText et= (EditText) itemView.findViewById(R.id.et_quantity);
        int qty= Integer.parseInt(et.getText().toString());
        qty+=c;
        et.setText(qty+"");


        makeVisible(itemView, qty==0?false:true);
        printToast(qty);
        String id=combo_id[i];
        _id=id;
        String cost=combo_cost[i];
        _cost=cost;

        if(mydb.ifPresent(id)){
            quantity=mydb.getQuantity(id);
            _quantity=quantity;
            mydb.updateItem(id, String.format("%.1f",(Double.valueOf(combo_cost[i])*(Double.valueOf(qty)))),qty+"");
        }
        else {
            _quantity="0";
            mydb.insertItem(combo_id[i], combo_name[i], String.format("%.1f", (Double.valueOf(combo_cost[i]) * (Double.valueOf(qty)))), qty + "");
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
        values.add(qty+"");
        params.add("flag");
        values.add("1");
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
                else {
                    //n+" Item \nTotal: Rs."+cost
                }
            }
        }).execute();


    }

    private void printToast(int qty) {
        final Toast toast = Toast.makeText(mContext, qty==0?"Removed":"Added", Toast.LENGTH_SHORT);
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        System.out.println("###3");

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_combo_simple, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterCombo.ViewHolder holder, int position) {
        int i=position;
        JSONObject combo;
        System.out.println("###1");
        try {
            System.out.println(position);
            combo=combos.getJSONObject(position);
            combo_id[i]=combo.optInt("combo_id")+"";
            combo_name[i]=capitalize(combo.optString("name"))+"";
            combo_cost[i]=combo.optString("cost")+"";
            combo_thumbnail[i]=combo.optString("thumbnail")+"";
            percent_off[i]=combo.optInt("percent_off")+"";
            min_qty[i]=combo.optInt("min_qty")+"";
            JSONArray array=combo.optJSONArray("items");
            combo_content[i]=fetchString(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(position);

        System.out.println("###2");

        TextView name= (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(combo_name[position]);



        TextView combo_costTv= (TextView) holder.itemView.findViewById(R.id.tv_price);
        combo_costTv.setText(combo_cost[position]+"/Item");

        TextView combo_offerTv = (TextView) holder.itemView.findViewById(R.id.tv_offer);

        int prcnt=Integer.parseInt(percent_off[i]);
        if(prcnt>0) {
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/italics.ttf");
            combo_offerTv.setText("Buy "+min_qty[i]+"Kg, Get "+percent_off[i]+"% Off");
            combo_offerTv.setTypeface(tf);
        }


        TextView combo_contentTv= (TextView) holder.itemView.findViewById(R.id.tv_content);
        combo_contentTv.setText(combo_content[position]);

        CircularImageView image= (CircularImageView) holder.itemView.findViewById(R.id.im_thumb);
        setImage(image, combo_thumbnail[position]);
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
            System.out.println(url);
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

    private String fetchString(JSONArray array) {
        StringBuilder output=new StringBuilder();
        try {

            for(int i=0;i<array.length();i++){
                JSONObject obj=array.getJSONObject(i);
                String name=obj.getString("item_name");
                String qty=obj.getString("item_qty");
                output.append(capitalize(name)+" "+qty+"Kg\n");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output.toString();
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

    @Override
    public int getItemCount() {
        return combo_id.length;
    }
}