package prashushi.farcon;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dell User on 6/30/2016.
 */
public class GetAddress extends AsyncTask<Void, Address, Address> {

    double lon, lat;
    Context mContext;
    public AsyncResponse delegate = null;
    String result;
    GetAddress(Context mContext, double lon, double lat, AsyncResponse delegate){
        this.lon=lon;
        this.lat=lat;
        this.mContext=mContext;
        this.delegate=delegate;
    }
    @Override
    protected Address doInBackground(Void... params) {

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        result = null;
        JSONObject jsonAddress=null;
        Address address=null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    lat, lon, 1);
            jsonAddress=new JSONObject();
            if (addressList != null && addressList.size() > 0) {
                address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                result = sb.toString();
                System.out.println("Complete Address:"+result);
           /*     try {
                    jsonAddress.put("street", address.getAddressLine(0)+"");
                    jsonAddress.put("area", address.getSubLocality());
                    jsonAddress.put("city", address.getLocality());
                    jsonAddress.put("pincode", address.getPostalCode());
                    jsonAddress.put("state", address.getAdminArea());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sb.append("Locality:"+address.getLocality()).append("\n");
                sb.append("PIN code:"+address.getPostalCode()).append("\n");
                sb.append("Country:"+address.getCountryName());

            */}
        } catch (IOException e) {
            Log.e("GetAddress", "Unable connect to Geocoder", e);
        }
        return address;
    }

    @Override
    protected void onPostExecute(Address add) {
        delegate.processFinish(add, result);
    }

    public interface AsyncResponse {
        void processFinish(Address add, String res);

    }
}
