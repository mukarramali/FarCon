package prashushi.farcon;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.victor.loading.newton.NewtonCradleLoading;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell User on 6/20/2016.
 */

public class ShopFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerview_shop;
    FloatingActionButton fab;
    boolean[] thread;
    JSONArray items = null;
    ArrayList<ShopItem> list;
    RecyclerAdapterShop adapter;
    SwipeRefreshLayout swipe_refresh;
    NewtonCradleLoading newtonCradleLoading;
    public ShopFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thread = getArguments().getBooleanArray("thread");
        Log.v("xxx", "thread in shop" + thread[0]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_shop, container, false);
        thread = getArguments().getBooleanArray("thread");
        Log.v("xxx", "thread in shop2" + thread[0]);
        recyclerview_shop= (RecyclerView) view.findViewById(R.id.recycler_shop);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview_shop.setLayoutManager(mLayoutManager);
        adapter = new RecyclerAdapterShop(getActivity(), getActivity().getSupportFragmentManager(), new ArrayList<ShopItem>(), thread);
        recyclerview_shop.setAdapter(adapter);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);
        swipe_refresh.post(new Runnable() {
                               @Override
                               public void run() {
                                   //    swipe_refresh.setRefreshing(true);
                                   callServer();
                               }
                           }
        );
        return view;
    }

    void callServer() {
        swipe_refresh.setRefreshing(true);
        String url=getString(R.string.local_host)+"item";
        new BackgroundTask(url, new ArrayList<String>(), new ArrayList<String>(), new BackgroundTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {

            /*   output="[\n" +
                       "  {\n" +
                       "    \"id\": \"2\",\n" +
                       "    \"item_name\": \"Potato\",\n" +
                       "    \"hindi_name\": \"Aloo\",\n" +
                       "    \"image\": \"/assets/images/gallery/pics/pPYYpic_4.png\",\n" +
                       "    \"detail_image\": \"\",\n" +
                       "    \"thumbnail\": \"/assets/images/gallery/thumbs/pPYYthumb_4.png\",\n" +
                       "    \"package\": [\n" +
                       "      {\n" +
                       "        \"id\": 2,\n" +
                       "        \"item_id\": \"2\",\n" +
                       "        \"package_qty\": \"1.00\",\n" +
                       "        \"item_cost\": \"17.00\",\n" +
                       "        \"item_min_qty\": null,\n" +
                       "        \"percent_off\": null,\n" +
                       "        \"terms\": null\n" +
                       "      },\n" +
                       "      {\n" +
                       "        \"id\": 63,\n" +
                       "        \"item_id\": \"2\",\n" +
                       "        \"package_qty\": \"0.50\",\n" +
                       "        \"item_cost\": \"10.00\",\n" +
                       "        \"item_min_qty\": null,\n" +
                       "        \"percent_off\": null,\n" +
                       "        \"terms\": null\n" +
                       "      }\n" +
                       "    ]\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"id\": \"3\",\n" +
                       "    \"item_name\": \"Tomato\",\n" +
                       "    \"hindi_name\": \"Tamatar\",\n" +
                       "    \"image\": \"/assets/images/gallery/pics/xtyNpic_5.png\",\n" +
                       "    \"detail_image\": \"\",\n" +
                       "    \"thumbnail\": \"/assets/images/gallery/thumbs/xtyNthumb_5.png\",\n" +
                       "    \"package\": [\n" +
                       "      {\n" +
                       "        \"id\": 3,\n" +
                       "        \"item_id\": \"3\",\n" +
                       "        \"package_qty\": \"1.00\",\n" +
                       "        \"item_cost\": \"55.00\",\n" +
                       "        \"item_min_qty\": 0,\n" +
                       "        \"percent_off\": 10,\n" +
                       "        \"terms\": null\n" +
                       "      },\n" +
                       "      {\n" +
                       "        \"id\": 6,\n" +
                       "        \"item_id\": \"3\",\n" +
                       "        \"package_qty\": \"0.50\",\n" +
                       "        \"item_cost\": \"30.00\",\n" +
                       "        \"item_min_qty\": null,\n" +
                       "        \"percent_off\": null,\n" +
                       "        \"terms\": null\n" +
                       "      }\n" +
                       "    ]\n" +
                       "  }]";
*/
                swipe_refresh.setRefreshing(false);
                if(output.contains("falsexxx"))
                {
                    Toast.makeText(getActivity(), getString(R.string.swipe_down), Toast.LENGTH_LONG).show();
                    return;
                }
                //JSONArray jsonArray= null;
                else {
                    try {
                        items = new JSONArray(output);
                        setAdapter();
                        Log.v("xxx", "thread in shop3" + thread[0]);
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), getString(R.string.swipe_down), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

//                swipe_refresh.set
            }
        }).execute();

    }

    void setAdapter() {
        if (items != null) {
            list = new ArrayList<>();
            try {
                for (int i = 0; i < items.length(); i++)
                    list.add(new ShopItem(items.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<ShopItem> newList = new ArrayList<>();
            newList.addAll(list);
            adapter = new RecyclerAdapterShop(getActivity(), getActivity().getSupportFragmentManager(), newList, thread);
            recyclerview_shop.setAdapter(adapter);
        }
    }

    @Override
    public void onRefresh() {

        callServer();
    }
}
