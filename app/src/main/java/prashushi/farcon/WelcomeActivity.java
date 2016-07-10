package prashushi.farcon;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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

public class WelcomeActivity extends AppCompatActivity{

    String number;
    SharedPreferences sPrefs;
    SharedPreferences.Editor editor;
    ProgressDialog pDialog;
    BackgroundTaskLoad backgroundTaskLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTransitions();
        checkLogged();
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etNumber=(EditText)findViewById(R.id.et_phone);
                number=etNumber.getText().toString();
                if(number.length()!=10) {
                    TextInputLayout til= (TextInputLayout) findViewById(R.id.input_layout);
                    til.setError("Please enter a valid number!");
                    return;
                }
                askOTP(number);
            }
        });
    }

    private void checkLogged() {
        sPrefs = getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);
        editor=sPrefs.edit();

        if(sPrefs.contains("logged")&&sPrefs.getBoolean("logged", false)) {

            System.out.println("Welcome Activity-> id:"+sPrefs.getString("id", "")+", phone:"+sPrefs.getString("phone", ""));
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

            finish();
        }
    }

    public  void askOTP(String num) { //change in otpActivity too
        number=num;
        String url=getString(R.string.local_host)+"phone/"+number;
        new BackgroundTaskLoad(url, this,new ArrayList<String>(), new ArrayList<String>(), new BackgroundTaskLoad.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("falsexxx")) {
                    Toast.makeText(WelcomeActivity.this, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject object = new JSONObject(output);
                    String id = object.optInt("id") + "";
                    String otp = object.optInt("otp") + "";
                    editor.clear().commit();
                    editor.putString("id", id);
                    editor.putString("otp", otp);
                    editor.putString("phone", number);
                    editor.putBoolean("status", object.optString("status").compareTo("new")==0);

                    editor.commit();
                    System.out.println("WA otp:" + sPrefs.getString("otp", "0"));
                    startActivity(new Intent(WelcomeActivity.this, OTPActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(WelcomeActivity.this, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                }

            }
        }).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
  /*  void setTransitions() {
        if(Build.VERSION.SDK_INT>=21)
        {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transitionExit = inflater.inflateTransition(R.transition.transition_up_out);
            getWindow().setExitTransition(transitionExit);
      //      Transition transitionEnter = inflater.inflateTransition(R.transition.transition_up_in);
        //    getWindow().setEnterTransition(transitionEnter);
        }
    }
*/    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            finish();
            System.exit(0);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            finish();
            System.exit(0);
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }


}
