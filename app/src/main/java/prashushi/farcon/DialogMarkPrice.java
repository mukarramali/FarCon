package prashushi.farcon;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Dell User on 7/11/2016.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class DialogMarkPrice extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mark);
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
                EditText etPrice=(EditText)findViewById(R.id.et_price);
                String price=etPrice.getText().toString();
                if (price.length()==0){
                    etPrice.setError("Can't be empty!");
                    return;
                }
                SharedPreferences sPrefs=getSharedPreferences(getString(R.string.S_PREFS), MODE_PRIVATE);

                String url=getString(R.string.local_host)+"mark";
                ArrayList<String> params=new ArrayList<>();
                ArrayList<String> values=new ArrayList<>();

                params.add("user_id");
                values.add(sPrefs.getString("id", "0"));
                params.add("item_package_id");
                values.add(getIntent().getExtras().getString("id"));
                params.add("price");
                values.add(getIntent().getExtras().getString("cost"));
                params.add("access_token");
                values.add(sPrefs.getString("access_token", "0"));
//                System.out.println("Received id:"+getIntent().getExtras().getString("id"));

                new BackgroundTaskPost(this, url, params, values, new BackgroundTaskPost.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if(output.contains("truexxx"))
                            Toast.makeText(DialogMarkPrice.this, getString(R.string.quote_rec), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(DialogMarkPrice.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).execute();
                break;
        }
    }
}
