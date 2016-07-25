package prashushi.farcon;

/**
 * Created by Dell User on 5/4/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

//1 425 3 425 3
public class RecyclerAdapterCombo extends Adapter<RecyclerAdapterCombo.ViewHolder> {

    ArrayList<ComboItem> combos;
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    EditText etQuantity;
    String id, quantity;
    String _quantity, _qty0, _id, _cost;
    int _i;
    private Context mContext;
    private DBHelper mydb ;
    boolean[] thread;
    final Typeface roboto;

    private LruCache<String, Bitmap> mMemoryCache;

    public RecyclerAdapterCombo(Context context, ArrayList<ComboItem> combos, boolean[] thread) {
        mContext = context;
        System.out.println("###");
        this.combos=combos;

        sPrefs=context.getSharedPreferences(context.getString(R.string.S_PREFS), context.MODE_PRIVATE);
        editor=sPrefs.edit();
        mydb = new DBHelper(context);
        this.thread = thread;
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

    private void makeVisible(View v, Boolean b) {

        v.findViewById(R.id.layout_count).setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        v.findViewById(R.id.bt_add).setVisibility(b ? View.INVISIBLE : View.VISIBLE);

    }

    void updateCart(int i, View itemView, int c){
        _i=i;


        EditText et= (EditText) itemView.findViewById(R.id.et_quantity);
        int qty= Integer.parseInt(et.getText().toString());
        qty+=c;
        combos.get(i).buycount(qty);
        et.setText(qty+"");


        makeVisible(itemView, qty!=0);
        printToast(c);
        String id = combos.get(i).id();
        _id=id;
        String cost = combos.get(i).cost();
        _cost=cost;

        if(mydb.ifPresent((toInt(id)*100)+"")){
            quantity=mydb.getQuantity((toInt(id)*100)+"");
            _quantity=quantity;
            mydb.updateItem((toInt(id) * 100) + "", String.format("%.0f", (Double.valueOf(combos.get(i).cost()) * (Double.valueOf(qty)))), qty + "");
        }
        else {
            _quantity="0";
            mydb.insertItem((toInt(combos.get(i).id()) * 100) + "", combos.get(i).name(), String.format("%.0f", (Double.valueOf(combos.get(i).cost()) * (Double.valueOf(qty)))), qty + "");
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
//        values.add(qty+"");
        values.add(c + "");

        params.add("item_combo");
        values.add("1");

        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));

        new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                System.out.println(output);
                if(!output.contains("truexxx")){
                    //remove the recent added item
                    mydb.updateItem((toInt(_id)*100)+"", _cost,_quantity);
                    new Utilities().checkIfLogged(output, mContext);
                }
                else {
                    //n+" Item \nTotal: Rs."+cost
                }
            }
        }).execute();


    }

    private Integer toInt(String id) {
    return Integer.parseInt(id);
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
        ComboItem combo;
        System.out.println("###1");
        combo = combos.get(position);

        System.out.println(position);

        System.out.println("###2");

        TextView name= (TextView) holder.itemView.findViewById(R.id.tv_name);
        name.setText(combo.name());
        name.setTypeface(roboto);

        TextView combo_costTv= (TextView) holder.itemView.findViewById(R.id.tv_price);
        combo_costTv.setText(mContext.getString(R.string.Rs) + combo.cost() + "/Combo");
        combo_costTv.setTypeface(roboto);

        TextView combo_offerTv = (TextView) holder.itemView.findViewById(R.id.tv_offer);
        combo_offerTv.setTypeface(roboto);

        int prcnt = Integer.parseInt(combo.percent_off());
        if(prcnt>0) {
            holder.itemView.findViewById(R.id.layout_offer).setVisibility(View.VISIBLE);
//            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/italics.ttf");
            combo_offerTv.setText("Buy " + combos.get(i).min_qty() + "Kg, Get " + combos.get(i).percent_off() + "% Off");
            combo_offerTv.setTypeface(roboto);
        }


        TextView combo_contentTv= (TextView) holder.itemView.findViewById(R.id.tv_content);
        combo_contentTv.setText(combos.get(i).content());
        combo_contentTv.setTypeface(roboto);
        if (combos.get(i).buycount() == 0)
            makeVisible(holder.itemView, false);
        else {
            makeVisible(holder.itemView, true);
            TextView etQ = (TextView) holder.itemView.findViewById(R.id.et_quantity);
            etQ.setText(combos.get(i).buycount() + "");
        }

        ImageView image = (ImageView) holder.itemView.findViewById(R.id.im_thumb);
        setImage(image, combos.get(i).thumbnail());
    }


    private void setImage(ImageView image, String s) {
        String url=mContext.getString(R.string.local_host_web)+s;

        DownloadImage downloadImage = new DownloadImage(image, thread, mMemoryCache, s, mContext);
        downloadImage.execute(url);
    }



    @Override
    public int getItemCount() {
        return combos.size();
    }

    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        int i=0;
        public ViewHolder(final View itemView) {
            super(itemView);
            System.out.println("###2");

            etQuantity=(EditText) itemView.findViewById(R.id.et_quantity);

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
            itemView.findViewById(R.id.bt_add).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = getAdapterPosition();
                    updateCart(i, itemView, +1);
//loading xml from anim folder
                    //You can now apply the animation to a view
             /*       ImageView im_thumb= (ImageView) itemView.findViewById(R.id.im_thumb);
                   // im_thumb.startAnimation(localAnimation);

                    setAllParentsClip(im_thumb, false);

                    View vv= (View) itemView.getParent().getParent();
                    int[] loc=new int[2];
                    vv.getLocationOnScreen(loc);
                    int deltaX = (loc[0]);
                    int deltaY = (loc[1]);

//                    int deltaX = (vv.getWidth());
//                    int deltaY = (vv.getHeight());

                    TranslateAnimation anim = new TranslateAnimation(
                            TranslateAnimation.ABSOLUTE, 0.0f,
                            TranslateAnimation.ABSOLUTE, deltaX,
                            TranslateAnimation.ABSOLUTE, 0.0f,
                            TranslateAnimation.ABSOLUTE, deltaY
                    );
                    anim.setFillAfter(false);
                    anim.setDuration(1000);

                    ScaleAnimation anim2=new ScaleAnimation(
                            1.0f,0.1f,
                            1.0f,0.1f);
                    anim2.setFillAfter(false);
                    anim2.setDuration(1000);

                    Animation together = AnimationUtils.loadAnimation(mContext, R.anim.together);


                    AnimationSet set=new AnimationSet(mContext, null);
                    //set.addAnimation(anim);
                    set.addAnimation(together);
                    set.setInterpolator(new DecelerateInterpolator());

                    im_thumb.setAnimation(together);

*/
                }
            });

        }
    }

    public ComboItem removeItem(int position) {
        final ComboItem item = combos.remove(position);
        notifyItemRemoved(position);
//        notifyDataSetChanged();
        return item;
    }

    public void addItem(int position, ComboItem model) {
        combos.add(position, model);
//        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ComboItem item = combos.remove(fromPosition);
        combos.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
//        notifyDataSetChanged();
    }

    public void animateTo(ArrayList<ComboItem> newCombos) {
        System.out.println("animate to:" + newCombos.size());
        applyAndAnimateRemovals(newCombos);
        applyAndAnimateAdditions(newCombos);
        applyAndAnimateMovedItems(newCombos);
        System.out.println("Remaining size:" + combos.size());
        notifyDataSetChanged();
    }

    private void applyAndAnimateRemovals(ArrayList<ComboItem> newCombos) {
        System.out.println("animateRemovals to:" + newCombos.size());
        for (int i = combos.size() - 1; i >= 0; i--) {
            final ComboItem model = combos.get(i);
            if (!newCombos.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<ComboItem> newCombos) {
        System.out.println("animateAdditions to:" + newCombos.size());
        for (int i = 0, count = newCombos.size(); i < count; i++) {
            final ComboItem model = newCombos.get(i);
            if (!combos.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<ComboItem> newCombos) {
        System.out.println("animateMove to:" + newCombos.size());
        for (int toPosition = newCombos.size() - 1; toPosition >= 0; toPosition--) {
            final ComboItem item = newCombos.get(toPosition);
            final int fromPosition = combos.indexOf(item);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }


    public static void setAllParentsClip(View v, boolean enabled) {
        while (v.getParent() != null && v.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) v.getParent();
            viewGroup.setClipChildren(enabled);
            viewGroup.setClipToPadding(enabled);
            v = viewGroup;
        }
    }

}