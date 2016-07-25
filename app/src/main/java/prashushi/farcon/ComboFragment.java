package prashushi.farcon;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ComboFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerview_combo;
    FloatingActionButton fab;
    boolean[] thread;
    JSONArray items = null;
    ArrayList<ComboItem> list;
    RecyclerAdapterCombo adapter;
    SwipeRefreshLayout swipe_refresh;

    public ComboFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thread = getArguments().getBooleanArray("thread");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_combo, container, false);
        thread = getArguments().getBooleanArray("thread");
        recyclerview_combo = (RecyclerView) view.findViewById(R.id.recycler_combo);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview_combo.setLayoutManager(mLayoutManager);
        adapter = new RecyclerAdapterCombo(getActivity(), new ArrayList<ComboItem>(), thread);
        recyclerview_combo.setAdapter(adapter);

        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);
        swipe_refresh.post(new Runnable() {
                               @Override
                               public void run() {
                                   //                            swipe_refresh.setRefreshing(true);
                                   callServer();
                               }
                           }
        );
        return view;
    }

    private void callServer() {

        String url = getString(R.string.local_host) + "combo";
        new BackgroundTask(url, new ArrayList<String>(), new ArrayList<String>(), new BackgroundTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                swipe_refresh.setRefreshing(false);
                if (output.contains("falsexxx")) {
                    Toast.makeText(getActivity(), getString(R.string.swipe_down), Toast.LENGTH_LONG).show();
                    return;
                } else {
                    try {
                        items = new JSONArray(output);
                        setAdapter();

                        System.out.println(output);

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), getString(R.string.swipe_down), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }


            }
        }).execute();

    }

    void setAdapter() {
        if (items != null) {
            list = new ArrayList<>();
            try {
                for (int i = 0; i < items.length(); i++)
                    list.add(new ComboItem(items.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<ComboItem> newList = new ArrayList<>();
            newList.addAll(list);
            adapter = new RecyclerAdapterCombo(getActivity(), newList, thread);
            recyclerview_combo.setAdapter(adapter);

        }

    }

    @Override
    public void onRefresh() {
        //swipe_refresh.setRefreshing(true);
        callServer();
    }
}
