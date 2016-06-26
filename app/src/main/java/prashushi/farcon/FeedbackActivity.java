package prashushi.farcon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_send).setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_send:
                EditText etName=(EditText)findViewById(R.id.et_name);
                String name=etName.getText().toString();
                if (name.length()==0){
                    TextInputLayout til=(TextInputLayout)findViewById(R.id.input_layout_1);
                    til.setError("Can't be empty!");
                }

                if(!format(name)){
                    etName.setError("Only alphabets!");
                    etName.requestFocus();
                    return;
                }

                EditText etEmail=(EditText)findViewById(R.id.et_email);
                String email=etEmail.getText().toString();
                EditText etMsg=(EditText)findViewById(R.id.et_msg);
                String msg=etMsg.getText().toString();
                if (msg.length()==0){
                    TextInputLayout til=(TextInputLayout)findViewById(R.id.input_layout_3);
                    til.setError("Can't be empty!");
                    break;
                }
                String url=getString(R.string.local_host)+"feed";
                ArrayList<String> params=new ArrayList<>();
                ArrayList<String> values=new ArrayList<>();

                params.add("id");
                values.add(getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE).getString("id", "0"));
                params.add("name");
                values.add(name);
                params.add("email");
                values.add(email);
                params.add("feedback");
                values.add(msg);

                new BackgroundTaskPost(url, params, values, new BackgroundTaskPost.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if(output.contains("truexxx"))
                            Toast.makeText(FeedbackActivity.this, getString(R.string.success_feed), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(FeedbackActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).execute();
                break;
        }
    }

    private boolean format(String name) {
        for(int i=0;i<name.length();i++){
            if(name.charAt(i)<'A'&&name.charAt(i)!=' ')
                return false;
        }
        return true;
    }
}
