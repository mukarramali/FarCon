package prashushi.farcon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dell User on 7/23/2016.
 */
public class OrderItemsFragment extends Fragment {
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerview_cart;
    SharedPreferences sPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fargment_order_items, container, false);
        recyclerview_cart = (RecyclerView) view.findViewById(R.id.recycler_order_items);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview_cart.setLayoutManager(mLayoutManager);
        sPrefs = getActivity().getSharedPreferences(getString(R.string.S_PREFS), getActivity().MODE_PRIVATE);
        callServer();
        return view;
    }

    private void callServer() {

        JSONObject header = null;
        String order_id = "";
        try {
            header = new JSONObject(getArguments().getString("order"));
            order_id = header.optString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String id = sPrefs.getString("id", "0");
        String url = getString(R.string.local_host) + "orderitem";

        ArrayList<String> params = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        //9808185808    8958050840
        params.add("id");
        values.add(id);
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));
        params.add("order_id");
        values.add(order_id);
        new BackgroundTaskPost(getActivity(), url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                if (output.contains("falsexxx"))
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                else {
                    System.out.println(output);
                    JSONArray jsonArray;
                    JSONObject jsonObject;
                    try {
                        jsonArray = new JSONArray(output);
                        if (jsonArray.length() > 0) {
                            RecyclerAdapterOrderItems recyclerAdapterOrderItems = new RecyclerAdapterOrderItems(getActivity(), jsonArray, new boolean[]{true});
                            recyclerview_cart.setAdapter(recyclerAdapterOrderItems);

                        } else {
                            Toast.makeText(getActivity(), "No items in your order!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {

                    }

                }
            }
        }).execute();
    }
}
