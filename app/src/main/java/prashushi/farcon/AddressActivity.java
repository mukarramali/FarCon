package prashushi.farcon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    EditText etName, etEmail, etPhone, etHouse, etStreet, etArea, etCity, etPin, etState, etMember;
    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    DBHelper mydb;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Location mLastLocation;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        mydb=new DBHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        try{
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }catch(Exception e){
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        findViewById(R.id.tv_order).setOnClickListener(this);
        sPref=getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        editor=sPref.edit();
        initET();
        findViewById(R.id.bt_loc).setOnClickListener(this);

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {



        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION , android.Manifest.permission.ACCESS_FINE_LOCATION },
                    MY_PERMISSIONS_REQUEST_LOCATION );
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            new GetAddress(this, longitude, latitude, new GetAddress.AsyncResponse() {
                @Override
                public void processFinish(Address address, String result) {
                    if(address!=null)
                    {
                        if(address.getSubLocality()!=null){
                            String area=address.getSubLocality();
                            etArea.setText(area);
                            int pos=result.indexOf(area);
                            String street=result.substring(0,pos-1);
                            etStreet.setText(street);

                        }
                        if(address.getLocality()!=null)
                            etCity.setText(address.getLocality());
                        if(address.getPostalCode()!=null)
                            etPin.setText(address.getPostalCode());
                        if(address.getAdminArea()!=null)
                            etState.setText(address.getAdminArea());

                    }
                    else
                        Toast.makeText(AddressActivity.this, "Sorry, Couldn't find your location!", Toast.LENGTH_LONG).show();
                }
            }).execute();

        } else {

            Toast.makeText(this, "Couldn't get the location. Make sure location is enabled on the device!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    displayLocation();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Toast.makeText(this, "Allow location permission to this app from Settings>Apps!", Toast.LENGTH_LONG).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }}

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Address Activity", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    private void initET() {
    etArea= (EditText) findViewById(R.id.et_area);
        etCity= (EditText) findViewById(R.id.et_city);
        etEmail= (EditText) findViewById(R.id.et_email);
        etHouse= (EditText) findViewById(R.id.et_house);
        etName= (EditText) findViewById(R.id.et_name);
        etPhone= (EditText) findViewById(R.id.et_phone);
        etPin= (EditText) findViewById(R.id.et_pin);
        etState= (EditText) findViewById(R.id.et_state);
        etStreet= (EditText) findViewById(R.id.et_street);
        etMember= (EditText) findViewById(R.id.et_member);

        etPhone.setText(sPref.getString("add_phone", ""));
        etName.setText(sPref.getString("add_name", ""));
        etEmail.setText(sPref.getString("add_email", ""));
        etMember.setText(sPref.getString("add_member", ""));
        etHouse.setText(sPref.getString("add_house", ""));
        etStreet.setText(sPref.getString("add_street", ""));
        etArea.setText(sPref.getString("add_area", ""));
        etCity.setText(sPref.getString("add_city", ""));
        etPin.setText(sPref.getString("add_pin", ""));
        etState.setText(sPref.getString("add_state", ""));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.bt_loc:

                displayLocation();
                break;
            case R.id.tv_order:

                String name, phone, email, member, house, street, area, city, pin, state;
                name=etName.getText().toString();
                phone=etPhone.getText().toString();
                email=etEmail.getText().toString();
                member=etMember.getText().toString();
                house=etHouse.getText().toString();
                street=etStreet.getText().toString();
                area=etArea.getText().toString();
                city=etCity.getText().toString();
                pin=etPin.getText().toString();
                state=etState.getText().toString();

                if(phone.length()!=10){
                    etPhone.setError("10 digits required");
                    etPhone.requestFocus();
                    break;
                }

                if(verify(name))
                {setErr(etName);break;}
                else
                removeErr(etName);

                if(!format(name)){
                    etName.setError("Only alphabets!");
                    etName.requestFocus();
                    return;
                }


                if(verify(phone))
                { setErr(etPhone);break;}
                else
                    removeErr(etPhone);
                if(verify(member))
                {   setErr(etMember);break;}
                else
                    removeErr(etMember);
                if(verify(house))
                {   setErr(etHouse);break;}
                else
                    removeErr(etHouse);
                if(verify(street))
                {   setErr(etStreet);break;}
                else
                    removeErr(etStreet);
                if(verify(area))
                {   setErr(etArea);break;}
                else
                    removeErr(etArea);
                if(verify(city))
                {   setErr(etCity);break;}
                else
                    removeErr(etCity);
                if(verify(pin))
                {   setErr(etPin);break;}
                else
                    removeErr(etPin);
                if(verify(state))
                {   setErr(etState);break;}
                else
                    removeErr(etState);


                editor.putString("add_name", name);
                editor.putString("add_phone",phone );
                editor.putString("add_email",email );
                editor.putString("add_member", member);
                editor.putString("add_house", house);
                editor.putString("add_street", street);
                editor.putString("add_area", area);
                editor.putString("add_city", city);
                editor.putString("add_pin", pin);
                editor.putString("add_state", state);
                editor.commit();


                ArrayList<String> params=new ArrayList<>();
                ArrayList<String> values=new ArrayList<>();

                String id=getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE).getString("id", "0");
                params.add("id");
                values.add(id);

                //System.out.println("id:"+id);

                params.add("name");
                values.add(name);

                params.add("email");
                values.add(email);
                params.add("phone_num");
                values.add(phone);
                params.add("house_no");
                values.add(house);
                params.add("street");
                values.add(street);
                params.add("area");
                values.add(area);
                params.add("city");
                values.add(city);
                params.add("state");
                values.add(state);
                params.add("pin");
                values.add(pin);
                params.add("no");
                values.add(member);

                params.add("access_token");
                values.add(sPref.getString("access_token", "0"));

                String url=getString(R.string.local_host)+"order";

                new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if(output.contains("truexxx")) {
                           String url2=getString(R.string.local_host)+"checkout";
                            ArrayList<String> params=new ArrayList<>();
                            ArrayList<String> values=new ArrayList<>();

                            String id=sPref.getString("id", "0");
                            params.add("id");
                            values.add(id);
                            params.add("access_token");
                            values.add(sPref.getString("access_token", "0"));


                            new BackgroundTaskPost(url2, params, values, new BackgroundTaskPost.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    if(!output.contains("falsexxx")) {
                                        Intent intent=new Intent(AddressActivity.this, ConfirmActivity.class);
                                        intent.putExtra("order_id", output);
                                        mydb.deleteDB();
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                        new Utilities().checkIfLogged(output, AddressActivity.this);
                                    }
                            }).execute();
                        }
                        else{

                            new Utilities().checkIfLogged(output, AddressActivity.this);

                        }
                    }
                }).execute();
                break;

        }
    }


    private void removeErr(EditText et) {
        et.setError(null);
    }

    private boolean verify(String st) {
            return st.length()==0;
    }
    private void setErr(EditText et) {
        et.setError("Can't be left empty!");
        et.requestFocus();
        return;
    }
    private boolean format(String name) {

        for(int i=0;i<name.length();i++){
            if(name.charAt(i)<'A'&&name.charAt(i)!=' ')
                return false;
        }
        return true;
    }

}
