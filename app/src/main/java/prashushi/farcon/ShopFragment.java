package prashushi.farcon;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Dell User on 6/20/2016.
 */

public class ShopFragment extends Fragment {
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerview_shop;
    FloatingActionButton fab;
    public ShopFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_shop, container, false);
        recyclerview_shop= (RecyclerView) view.findViewById(R.id.recycler_shop);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview_shop.setLayoutManager(mLayoutManager);
        String url=getString(R.string.local_host)+"item";
        new BackgroundTaskLoad(url,getActivity(), new ArrayList<String>(), new ArrayList<String>(), new BackgroundTaskLoad.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                output="[{\"id\":1,\"item_name\":\"gtf\",\"image\":\"\",\"thumbnail\":\"\",\"item_min_qty\":2,\"percent_off\":10,\"item_id\":1,\"terms\":\"dfghs\",\n" +
                        "\"package\":[{\"id\":1,\"item_id\":1,\"package_qty\":2,\"item_package_id\":1,\"item_cost\":12},{\"id\":2,\"item_id\":1,\"package_qty\":123,\"item_package_id\":2,\"item_cost\":109}]}]";
                if(output.contains("falsexxx"))
                {
                    Toast.makeText(getActivity(), "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    return;
                }  JSONArray jsonArray= null;
                try {
                    jsonArray = new JSONArray(output);
                    System.out.println(output);
                    final RecyclerAdapterShop adapter=new RecyclerAdapterShop(getActivity(), getActivity().getSupportFragmentManager(),jsonArray);
                    recyclerview_shop.setAdapter(adapter);
                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        }).execute();
        return view;
    }

}
