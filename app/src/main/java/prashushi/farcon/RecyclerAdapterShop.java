package prashushi.farcon;

/**
 * Created by Dell User on 5/4/2016.
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
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
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
public class RecyclerAdapterShop extends Adapter<RecyclerAdapterShop.ViewHolder> implements ListPackageDialog.OnCompleteListener{

    JSONArray items;
    String[] item_id, item_name, item_cost, item_thumbnail, percent_off, min_qty, item_pkg_qty;
    JSONArray[] item_package;
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
    public RecyclerAdapterShop(Context context, FragmentManager fragmentManager, JSONArray items) {
        mContext = context;
        this.fragmentManager=fragmentManager;
        this.items=items;
        System.out.println("***");
        item_id=new String[items.length()];
        item_thumbnail=new String[items.length()];
        item_name=new String[items.length()];
        item_cost=new String[items.length()];
        percent_off=new String[items.length()];
        min_qty=new String[items.length()];
        item_package=new JSONArray[items.length()];
        item_pkg_qty=new String[items.length()];

        sPrefs=context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        //editor=sPrefs.edit();
        mydb = new DBHelper(context);
    }


    private void makeVisible(View v, Boolean b) {

        v.findViewById(R.id.bt_minus).setVisibility(b?View.VISIBLE:View.INVISIBLE);
        v.findViewById(R.id.et_quantity).setVisibility(b?View.VISIBLE:View.INVISIBLE);


    }

    void updateCart(int i, View itemView, int c){
        _i=i;


        EditText et= (EditText) itemView.findViewById(R.id.et_quantity);
        int qty= Integer.parseInt(et.getText().toString());
        qty+=c;
        et.setText(qty+"");


        makeVisible(itemView, qty!=0);
        printToast(qty);
        String id=item_id[i];
        _id=id;
        String cost=item_cost[i];
        _cost=cost;

        if(mydb.ifPresent(id)){
            quantity=mydb.getQuantity(id);
            _quantity=quantity;
            mydb.updateItem(id, String.format("%.1f",(Double.valueOf(item_cost[i])*(Double.valueOf(qty)))),qty+"");
        }
        else {
            _quantity="0";
            mydb.insertItem(item_id[i], item_name[i], String.format("%.1f", (Double.valueOf(item_cost[i]) * (Double.valueOf(qty)))), qty + "");
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
    public int getItemCount() {
        //      return shop.size();
        return items.length();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
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
            System.out.println(i);
            item=items.getJSONObject(i);
            item_name[i]=capitalize(item.optString("item_name"))+"";
            item_thumbnail[i]=item.optString("thumbnail")+"";
            percent_off[i]=item.optInt("percent_off")+"";
            min_qty[i]=item.optInt("item_min_qty")+"";
            item_package[i]=item.optJSONArray("package");
            JSONObject first=getPackageAt(0, item_package[i]);
            item_id[i]=first.optInt("id")+"";
            item_cost[i]=first.optString("item_cost")+"";
            item_pkg_qty[i]=first.optString("package_qty")+"";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView name= (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(item_name[i]);


        printSizePrice(holder.itemView, item_cost[i], item_pkg_qty[i]);

        TextView item_offerTv = (TextView) holder.itemView.findViewById(R.id.tv_offer);

        int prcnt=Integer.parseInt(percent_off[i]);
        if(prcnt>0) {
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/italics.ttf");
            item_offerTv.setText("Buy "+min_qty[i]+"Kg, Get "+percent_off[i]+"% Off");
            item_offerTv.setTypeface(tf);
        }

        ImageView image= (ImageView) holder.itemView.findViewById(R.id.im_thumb);
        setImage(image, item_thumbnail[position]);
    }

    private JSONObject getPackageAt(int i, JSONArray jsonArray) {
        JSONObject obj=new JSONObject();
        try {
           obj=jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


    @Override
    public void onComplete(String id1, String size1, String price1, int pos) {
        item_id[pos]=id1;
        item_cost[pos]=price1;
        item_pkg_qty[pos]=size1;

        printSizePrice(clickedItem, price1, size1);
        System.out.println("in recycler");
    }

    private void printSizePrice(View clickedItem, String price1, String size1) {
        TextView item_costTv= (TextView) clickedItem.findViewById(R.id.tv_price);
        item_costTv.setText(price1+" "+mContext.getResources().getString(R.string.Rs));

        TextView item_sizeTv= (TextView) clickedItem.findViewById(R.id.tv_spinner);
        item_sizeTv.setText(getSizeTag(Double.valueOf(size1)));
    }

    private String getSizeTag(Double size_d) {
        String tag="Kg";
        if(size_d<1.0)
        {
            size_d*=1000;
            tag="grams";
        }
        return size_d+" "+tag;
    }
    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        int i=0;
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public ViewHolder(final View itemView) {
            super(itemView);
            System.out.println("***4");
            etQuantity=(EditText) itemView.findViewById(R.id.et_quantity);


            itemView.findViewById(R.id.spinner).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> size, price, id;
                    size=new ArrayList<String>();
                    price=new ArrayList<String>();
                    id=new ArrayList<String>();
                    for(int i=0;i<item_package[getAdapterPosition()].length();i++){
                        JSONObject temp=getPackageAt(i, item_package[getAdapterPosition()]);
                        size.add(temp.optString("package_qty"));
                        id.add(temp.optInt("id")+"");
                        price.add(temp.optString("item_cost")+"");
                    }
                    ListPackageDialog listPackageDialog=ListPackageDialog.newInstance(id, size, price, getAdapterPosition(), item_id, item_cost, item_pkg_qty);
                    listPackageDialog.show(fragmentManager, null);
                    clickedItem=v;
                }
            });

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
                    updateCart(i, itemView, 1);

                }
            });

            itemView.findViewById(R.id.mark).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=getAdapterPosition();
                    Intent intent=new Intent(mContext,DialogMarkPrice.class);
                    intent.putExtra("id", item_id[i]);
                    intent.putExtra("cost", item_cost[i]);
                    intent.putExtra("size", item_pkg_qty[i]);
                    mContext.startActivity(intent);

                }
            });

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

  */       }
    }

    class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView im;

        public DownloadImage(ImageView pstr) {
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

    private void setImage(ImageView image, String s) {

        String url=mContext.getString(R.string.local_host_web)+s;
        DownloadImage downloadImage=new DownloadImage(image);
        downloadImage.execute(url);
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