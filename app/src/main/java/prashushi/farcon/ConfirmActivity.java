package prashushi.farcon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Dell User on 6/21/2016.
 */
public class ConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        try {
            mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        }catch(Exception e){
            e.printStackTrace();
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");
        TextView tv1= (TextView) findViewById(R.id.tv1);
        try {
            String order_id = getIntent().getExtras().getString("order_id");
            tv1.setText(getString(R.string.confirm_order) + "\nYour Order Id " + order_id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

}
