package prashushi.farcon;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 999;
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    EditText et;
    String number = "";
    Button bt_verify;
    Boolean _isNew;
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String code = intent.getExtras().getString("otp");
                et.setText(code);
                bt_verify.performClick();
            }catch (Exception n){
                n.printStackTrace();
            }
        }
    };

    //IncomingSms broadcast_receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // startService(new Intent(this, IncomingSms.class));
      //  setTransitions();
        setContentView(R.layout.activity_otp);
        sPrefs = getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        editor = sPrefs.edit();
        et = (EditText) findViewById(R.id.et_otp);
        bt_verify= (Button) findViewById(R.id.tv_verify);
        bt_verify.setOnClickListener(this);
        findViewById(R.id.tv_resend).setOnClickListener(this);
        findViewById(R.id.tv_num_enter).setOnClickListener(this);
        //IntentFilter filter1 = new IntentFilter();
        //filter1.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver, new IntentFilter("BroadcastOTP"));
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.READ_SMS , Manifest.permission.RECEIVE_SMS },
                    MY_PERMISSIONS_REQUEST_LOCATION );
            return;
        }

    }

    Boolean checkOTP(String otp) {
        String otp1 = sPrefs.getString("otp", "");
        System.out.println("Saved otp:" + otp1);
        System.out.println("Entered otp:" + otp);
        if (otp.compareTo(otp1) == 0) {
//            unregisterReceiver(broadcast_receiver);
        return true;
        }
        else {
            Toast.makeText(this, "Wrong OTP!", Toast.LENGTH_LONG).show();
        return false;
        }
    }


    private void askReferral(Boolean temp) {
        _isNew = temp;
        String id = sPrefs.getString("id", "0");
        if (id.compareTo("0")==0) {
            errorRestart();
            return;
        }
        String url = getString(R.string.local_host) + "generate_referal"; //{"referal_code"="1235"}


        ArrayList<String> params = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
//9808185808    8958050840
        params.add("id");
        values.add(id);
        params.add("access_token");
        values.add(sPrefs.getString("access_token", "0"));


        new BackgroundTaskPost(this, url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("falsexxx")) {
                    Toast.makeText(OTPActivity.this, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject object = new JSONObject(output);
                    String ref = object.getString("referal_code");
                    SharedPreferences sPrefs = getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPrefs.edit();
                    editor.putString("referral_code", ref);
                    editor.commit();
                    //startActivity(new Intent(OTPActivity.this, ReferralActivity.class));
                   // finish();
                    if (_isNew) {
                        startActivity(new Intent(OTPActivity.this, ReferralActivity.class));
                        finish();
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    } else {
                        Intent intent = new Intent(OTPActivity.this, HomeActivity.class);
                        intent.putExtra("update", true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OTPActivity.this, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                }

            }
        }).execute();

    }

    private void errorRestart() {
        Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

    }

    boolean validatePhone(String phone) {
        return phone.length() == 10;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_verify:
                unregisterReceiver(broadcastReceiver);
                if(!checkOTP(et.getText().toString()))
                    break;

                String url=getString(R.string.local_host)+"oauth/access_token";
                ArrayList<String> params=new ArrayList<>();
                ArrayList<String> values=new ArrayList<>();

                params.add("grant_type");
                values.add("client_credentials");

                params.add("client_id");
                values.add("user_id");

                params.add("client_secret");
                values.add("user_password");

                new BackgroundTaskPost(this, url, params, values, new BackgroundTaskPost.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if(!output.contains("falsexxx")) {

                            JSONObject jsonObject= null;
                            try {
                                jsonObject = new JSONObject(output);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String access_token=jsonObject.optString("access_token");
                            Boolean isNew = sPrefs.getBoolean("status", false);
                            editor.putString("access_token", access_token);
                            editor.putBoolean("logged", true);
                            editor.commit();
                            askReferral(isNew);

                        }

                    }
                }).execute();

                break;
            case R.id.tv_resend:
                String phone = sPrefs.getString("phone", "0");
                if (!validatePhone(phone))
                    errorRestart();
                askOTP(phone);
                break;
            case R.id.tv_num_enter:

                startActivity(new Intent(this, WelcomeActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
                break;
        }
    }

    public void askOTP(String num) {   //change in welcomeActivity too
        number = num;
        String url = getString(R.string.local_host) + "phone";
        ArrayList<String> params = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
//9808185808    8958050840
        params.add("phoneno");
        values.add(num);
        params.add("token");
        values.add("qwerty12345");
        new BackgroundTaskPost(this, url, params, values, new BackgroundTaskPost.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("falsexxx")) {
                    Toast.makeText(OTPActivity.this, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject object = new JSONObject(output);
                    String id = object.optInt("id") + "";
                    String otp = object.optInt("otp") + "";
                    String status = object.optString("status", "0");

                    editor.clear().commit();
                    editor.putString("id", id);
                    editor.remove("otp");
                    editor.putString("otp", otp);
                    editor.putString("phone", number);
                    if (status.compareTo("new")==0)
                        editor.putBoolean("status", true);
                    else
                        editor.putBoolean("status", false);
                    editor.commit();
                    //     startActivity(new Intent(OTPActivity.this, OTPActivity.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OTPActivity.this, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                }

            }
        }).execute();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            finish();
            //push from top to bottom

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
         //       registerReceiver(broadcastReceiver, new IntentFilter("BroadcastOTP"));

                break;

        }
    }
}
