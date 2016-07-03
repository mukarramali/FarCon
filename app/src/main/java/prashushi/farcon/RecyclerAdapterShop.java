package prashushi.farcon;

/**
 * Created by Dell User on 5/4/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
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
public class RecyclerAdapterShop extends Adapter<RecyclerAdapterShop.ViewHolder> {

    JSONArray items;
    String[] item_id, item_name, item_cost, item_thumbnail, percent_off, min_qty;
    SharedPreferences sPrefs;
    //SharedPreferences.Editor editor;
    EditText etQuantity;
    String id, quantity;
    String _quantity, _qty0, _id, _cost;
    int _i;
    private Context mContext;
    private DBHelper mydb ;
    public RecyclerAdapterShop(Context context, JSONArray items) {
        mContext = context;
        this.items=items;
        System.out.println("***");
        item_id=new String[items.length()];
        item_thumbnail=new String[items.length()];
        item_name=new String[items.length()];
        item_cost=new String[items.length()];
        percent_off=new String[items.length()];
        min_qty=new String[items.length()];
        sPrefs=context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        //editor=sPrefs.edit();
        mydb = new DBHelper(context);
    }

public static void setAllParentsClip(View v, boolean enabled) {
        while (v.getParent() != null && v.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) v.getParent();
            viewGroup.setClipChildren(enabled);
            viewGroup.setClipToPadding(enabled);
            v = viewGroup;
        }
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


    private void createAnimation(View itemView) {
        System.out.println("Animation");
        int[] curr=new int[2];
        ImageView image= (ImageView) itemView.findViewById(R.id.im_thumb);

        image.getLocationOnScreen(curr);
        float startX = curr[0];
        float startY = curr[1];
        Button add= (Button) itemView.findViewById(R.id.bt_add);
        int toLoc[] = new int[2];
        add.getLocationOnScreen(toLoc);
        float destX = toLoc[0];
        float destY = toLoc[1];
        Animation.AnimationListener animL = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //this is just a method call you can create to delete the animated view or hide it until you need it again.
                //clearAnimation();
            }
        };
/*
//1st
        Animations anim = new Animations();
        Animation a = anim.fromAtoB(startX, startY, destX, destY, animL,850);
        image.setAnimation(a);
        a.startNow();
*/
//3rd

        AnimationSet animationSet = new AnimationSet(true);

        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0, Animation.INFINITE,400,
                Animation.RELATIVE_TO_SELF,0, Animation.INFINITE,-100);
        ta.setDuration(1000);
        animationSet.addAnimation(ta);

        image.startAnimation(animationSet);

//2nd
       // Animation move = AnimationUtils.loadAnimation(mContext, R.anim.move_right);
//        image.setAnimation(move_right);
      //  image.startAnimation(move);
        setAllParentsClip(image, false);
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
            System.out.println(i);
            item=items.getJSONObject(i);
            item_id[i]=item.optInt("item_id")+"";
            item_name[i]=capitalize(item.optString("item_name"))+"";
            item_cost[i]=item.optString("item_cost")+"";
            item_thumbnail[i]=item.optString("thumbnail")+"";
            percent_off[i]=item.optInt("percent_off")+"";
            min_qty[i]=item.optInt("item_min_qty")+"";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView name= (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(item_name[i]);


        TextView item_costTv= (TextView) holder.itemView.findViewById(R.id.tv_price);
        item_costTv.setText("Rs."+item_cost[i]+"/Kg");

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
        }
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

}