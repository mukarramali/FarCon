package prashushi.farcon;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    EditText et;
    String number = "";
    IncomingSms broadcast_receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // startService(new Intent(this, IncomingSms.class));
      //  setTransitions();
        setContentView(R.layout.activity_otp);
        sPrefs = getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        editor = sPrefs.edit();
        et = (EditText) findViewById(R.id.et_otp);
        findViewById(R.id.tv_verify).setOnClickListener(this);
        findViewById(R.id.tv_resend).setOnClickListener(this);
        findViewById(R.id.tv_num_enter).setOnClickListener(this);
    /*    broadcast_receiver = new IncomingSms();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcast_receiver, filter1);
    */}


    Boolean checkOTP(String otp) {
        String otp1 = sPrefs.getString("otp", "");
        System.out.println("Saved otp:" + otp1);
        System.out.println("Entered otp:" + otp);
        if (otp.compareTo(otp1)==0)
        { askReferral();
//            unregisterReceiver(broadcast_receiver);
        return true;
        }
        else {
            Toast.makeText(this, "Wrong OTP!", Toast.LENGTH_LONG).show();
        return false;
        }
    }

    public void recivedSms(String message) {

        System.out.println("Received msg:" + message);
        sPrefs = getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        String otp = sPrefs.getString("otp", "0");
        if (otp.compareTo("0")==0) {
            errorRestart();
            return;
        }
        if (message.contains(otp)) {
            //     stopService(new Intent(this, IncomingSms.class));
       //     unregisterReceiver(broadcast_receiver);
            askReferral();
        }

    }

    private void askReferral() {
        String id = sPrefs.getString("id", "0");
        if (id.compareTo("0")==0) {
            errorRestart();
            return;
        }
        String url = getString(R.string.local_host) + "generate_referal/" + id; //{"referal_code"="1235"}
        new BackgroundTaskLoad(url, this,new ArrayList<String>(), new ArrayList<String>(), new BackgroundTaskLoad.AsyncResponse() {
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
    }

    boolean validatePhone(String phone) {
        return phone.length() == 10;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_verify:

                if(!checkOTP(et.getText().toString()))
                    break;
                Boolean isNew = sPrefs.getBoolean("status", false);
                editor.putBoolean("logged", true);
                editor.commit();
                if (isNew) {
                    startActivity(new Intent(OTPActivity.this, ReferralActivity.class));
                    finish();
                    System.out.println("***");
                } else {
                    startActivity(new Intent(OTPActivity.this, HomeActivity.class));
                    System.out.println("###");
                    finish();
                }

                break;
            case R.id.tv_resend:
                String phone = sPrefs.getString("phone", "0");
                if (!validatePhone(phone))
                    errorRestart();
                askOTP(phone);
                break;
            case R.id.tv_num_enter:

                onBackPressed();

                break;
        }
    }

    public void askOTP(String num) {   //change in welcomeActivity too
        number = num;
        String url = getString(R.string.local_host) + "phone/" + number;
        new BackgroundTaskLoad(url,this, new ArrayList<String>(), new ArrayList<String>(), new BackgroundTaskLoad.AsyncResponse() {
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

  /*  void setTransitions() {
    if(Build.VERSION.SDK_INT>=21)
    {
        TransitionInflater inflater = TransitionInflater.from(this);
        Transition transitionExit = inflater.inflateTransition(R.transition.transition_up_out);
        getWindow().setExitTransition(transitionExit);
        Transition transitionEnter = inflater.inflateTransition(R.transition.transition_up_in);
        getWindow().setEnterTransition(transitionEnter);
    }
}*/
}
