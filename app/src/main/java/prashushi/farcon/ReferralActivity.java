package prashushi.farcon;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ReferralActivity extends AppCompatActivity implements View.OnClickListener {

    Button tv_verify;
    EditText etReferral;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTransitions();
        setContentView(R.layout.activity_referral);

        findViewById(R.id.tv_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHome();
            }
        });
        tv_verify= (Button) findViewById(R.id.bt_verify);
        etReferral= (EditText) findViewById(R.id.et_referral);
        tv_verify.setOnClickListener(this);
    }

    void gotoHome() {
        startActivity(new Intent(ReferralActivity.this, HomeActivity.class));
        finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            gotoHome();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
/*    void setTransitions() {
        if(Build.VERSION.SDK_INT>=21)
        {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transitionExit = inflater.inflateTransition(R.transition.transition_up_out);
            getWindow().setExitTransition(transitionExit);
            Transition transitionEnter = inflater.inflateTransition(R.transition.transition_up_in);
            getWindow().setEnterTransition(transitionEnter);
        }
    }
*/
    @Override
    public void onClick(View v) {
        String code=etReferral.getText().toString();
        if(code.length()==0){
            etReferral.setError(getString(R.string.left_empty));
            return;
        }
        String url=getString(R.string.local_host)+"referal/"+getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE).getString("id", "0")+"/"+code;
        new BackgroundTaskLoad(url, this,new ArrayList<String>(), new ArrayList<String>(), new BackgroundTaskLoad.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if(output.contains("truexxx")){
                    Toast.makeText(ReferralActivity.this, getString(R.string.rewarded), Toast.LENGTH_LONG).show();
                    tv_verify.setVisibility(View.INVISIBLE);
                    etReferral.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(ReferralActivity.this, HomeActivity.class));
                    finish();
                }
                else
                    Toast.makeText(ReferralActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();

            }
        }).execute();
    }
}

