package prashushi.farcon;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

public class ComboFragment extends Fragment {
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerview_combo;
    FloatingActionButton fab;
    public ComboFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_combo, container, false);
        recyclerview_combo= (RecyclerView) view.findViewById(R.id.recycler_combo);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview_combo.setLayoutManager(mLayoutManager);
        String url=getString(R.string.local_host)+"combo";
        new BackgroundTaskLoad(url,getActivity(), new ArrayList<String>(), new ArrayList<String>(), new BackgroundTaskLoad.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                if(output.contains("falsexxx"))
                {
                    Toast.makeText(getActivity(), "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    return;
                }  JSONArray jsonArray= null;
                try {
                    String out="[{\"combo_id\":4,\"name\":\"combo 2\",\"cost\":45," +
                            "\"thumbnail\":\"\",\"items\":[{\"id\":1,\"item_name\"" +
                            ":\"apple\",\"item_qty\":1}]},{\"combo_id\":5,\"name\"" +
                            ":\"combo 2\",\"cost\":48,\"thumbnail\":\"\",\"items\":[" +
                            "{\"id\":1,\"item_name\":\"apple\",\"item_qty\":1},{\"id\":1," +
                            "\"item_name\":\"apple\",\"item_qty\":2}]},{\"combo_id\":6," +
                            "\"name\":\"combo4\",\"cost\":4,\"thumbnail\":\"\\/assets\\/" +
                            "images\\/gallery\\/thumbs\\/NRvmthumb_a1.jpg\",\"items\":[{\"" +
                            "id\":1,\"item_name\":\"apple\",\"item_qty\":5},{\"id\":2,\"" +
                            "item_name\":\"mango\",\"item_qty\":4}]}]";
                    jsonArray = new JSONArray(output);
                    System.out.println(output);
                    System.out.println("###");
                    final RecyclerAdapterCombo adapter=new RecyclerAdapterCombo(getActivity(), jsonArray);
                    System.out.println("###");
                    recyclerview_combo.setAdapter(adapter);
                    System.out.println("###");
                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        }).execute();
        return view;
    }

}
