package prashushi.farcon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dell User on 7/23/2016.
 */
public class OrderSummaryFragment extends Fragment {
    SharedPreferences sPrefs;
    String order_id;
    TextView tv_name, tv_add1, tv_add2, tv_add3, tv_phone, tv_email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fargment_order_summary, container, false);
        sPrefs = getActivity().getSharedPreferences(getString(R.string.S_PREFS), getActivity().MODE_PRIVATE);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_add1 = (TextView) view.findViewById(R.id.tv_address1);
        tv_add2 = (TextView) view.findViewById(R.id.tv_address2);
        tv_add3 = (TextView) view.findViewById(R.id.tv_address3);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_email = (TextView) view.findViewById(R.id.tv_email);


        JSONObject header = null;
        try {
            header = new JSONObject(getArguments().getString("order"));
            order_id = header.optString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String id = sPrefs.getString("id", "0");
        String url = getString(R.string.local_host) + "orderaddress";

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
                    try {
                        JSONArray temp = new JSONArray(output);
                        JSONObject obj = temp.getJSONObject(0);
                        String name = obj.optString("name");
                        String add1 = obj.optString("house_no") + ", ";
                        add1 += obj.optString("street");
                        String add2 = obj.optString("area") + ", ";
                        add2 += obj.optString("city");
                        String add3 = obj.optString("pincode") + " - ";
                        add3 += obj.optString("state");
                        String phone_num = obj.optString("phone_num");
                        String email = obj.optString("email");

                        tv_name.setText(name);
                        tv_add1.setText(add1);
                        tv_add2.setText(add2);
                        tv_add3.setText(add3);
                        tv_phone.setText(phone_num);
                        tv_email.setText(email);

                        System.out.println("set text views");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute();


        return view;
    }
}
